package com.fiats.content.utils;

import com.fiats.content.constant.ContentConstant;
import com.fiats.content.constant.ContentErrorCode;
import com.fiats.content.payload.ContParamDTO;
import com.fiats.content.service.ContParamService;
import com.fiats.exception.NeoFiatsException;
import com.fiats.tmgcoreutils.constant.Constant;
import com.fiats.tmgcoreutils.constant.VelocityParamKeyEnum;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import com.neo.exception.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ContractHelper {

    @Autowired
    ContParamService contParamService;

    private static Map<String, List<String>> mapTableVariables = new HashMap<>();

    // change this to DB config for multiple tables detection
    @Value("#{${snapshot.table.variables}}")
    public void initTableVariableMap(Map<String, String> tableVariables) {
        if (!CollectionUtils.isEmpty(tableVariables)) {
            mapTableVariables = tableVariables.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> Arrays.asList(e.getValue().split(Constant.COMMA))));
        }
    }

    public static Map<String, List<String>> buildAvailableVariablesForContract() {
        return mapTableVariables.entrySet().stream()
                // or more conditions here for multiple table rows e.g (key == coupons (table#1) || key == rates (table#2))
                .filter(e -> e.getKey().equals(VelocityParamKeyEnum.COUPON_TABLE_MODEL_KEY.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String findAndReplaceTableRowsWithVelocityTemplate(String templateCode, String templateName, String docContent,
                                                              Map<String, List<String>> definedTableVariables) {

        try {

            if (!StringUtils.hasText(docContent) || CollectionUtils.isEmpty(definedTableVariables)) {
                return docContent; // nothing to do
            }

            List<String> velocityCountVariables = contParamService.findAllContParam().stream()
                    .filter(p -> p.getParamSource().toLowerCase().contains("velocitycount"))
                    .map(ContParamDTO::getCode)
                    .collect(Collectors.toList());

            // remove previous velocity key + $Table to insert again. Word conversion messed its order up
            // then split by table tabs
            String[] docContentParts = docContent
                    .replaceAll(ContentConstant.REGEX_TO_REMOVE_VELOCITY_FOREACH, Constant.EMPTY)
                    .split(ContentConstant.TABLE_HTML_END_TAG);
            StringJoiner modifiedDocContentSJ = new StringJoiner(ContentConstant.TABLE_HTML_END_TAG);
            for (String docContentPart : docContentParts) {

                for (Map.Entry<String, List<String>> entry : definedTableVariables.entrySet()) {
                    String key = entry.getKey();
                    List<String> variables = entry.getValue();
                    log.info("Using key {} and variables {} to search for table rows ...",
                            key, LoggingUtils.objToStringIgnoreEx(variables));

                    List<String> allParts = collectTableRows(docContentPart, templateCode, templateName, variables);
                    log.info("For templateCode {} templateName {}, done extracting table row {}", templateCode, templateName, allParts);

                    // either not parseable, already contain the velocity loop or incorrect logic
                    if (allParts == null) {
                        continue;
                    }

                    // added velocity loop
                    // e.g: #foreach( $Table in $coupons ) #$velocityCount - <b>$Table.test1</b> and <b>$Table.test2</b> #end
                    StringBuilder modifiedContent = new StringBuilder();
                    modifiedContent.append(allParts.get(0)); // first part keep the same
                    modifiedContent.append(CommonUtils.format(ContentConstant.FOREACH_START_FORMAT, key));
                    modifiedContent.append(replaceVariableWithTableVariables(allParts.get(1), variables,
                            velocityCountVariables));
                    modifiedContent.append(ContentConstant.FOREACH_END_FORMAT);
                    modifiedContent.append(allParts.get(2)); // last part keep the same
                    log.info("Done modified the row for key {} of template id {} templateName {}", key, templateCode, templateName);

                    docContentPart = modifiedContent.toString(); // update reference for new loop
                }

                modifiedDocContentSJ.add(docContentPart);
            }

            return modifiedDocContentSJ.toString();

        } catch (Exception e) {

            log.error("Failed to find table rows inside template id {} and templateName {}", templateCode, templateName);
            log.error(e.getMessage(), e);
            return docContent;
        }
    }

    private String replaceVariableWithTableVariables(String input, List<String> variables, List<String> indexVariables) {

        if (!StringUtils.hasText(input)) {
            throw new NeoFiatsException(ContentErrorCode.TEMPLATE_CONTENT_EMPTY, "Empty table row!");
        }

        for (String variable : variables) {
            boolean indexVariable = !CollectionUtils.isEmpty(indexVariables) && indexVariables.contains(variable);
            String valueToReplace = indexVariable ? "$velocityCount"
                    : ContentConstant.TABLE_TEMPORARY_KEY.concat(".").concat(variable);
            input = input.replace("$".concat(variable), valueToReplace);
        }

        return input;
    }

    private List<String> collectTableRows(String content, String templateCode, String templateName, List<String> variables) {

        if (CollectionUtils.isEmpty(variables)) {
            return null;
        }

        // build regex based on available variables inside the target table row
        Pattern pattern = Pattern.compile(buildRegexForTableRows(variables));

        // collect a list of table rows
        Matcher matcher = pattern.matcher(content);

        if (matcher.matches() && matcher.groupCount() == 3) {

            log.info("Template Id {} templateName {} matched with table detection regex. Exacting the rows ...",
                    templateCode, templateName);
            List<String> allParts = new ArrayList<>();
            allParts.add(matcher.group(1));
            allParts.add(matcher.group(2)); // target table row
            allParts.add(matcher.group(3));
            return allParts;
        }

        log.info("Content for templateID {} templateName {} is unparseable. Avoid modifying ...", templateCode, templateName);
        return null;
    }

    private String buildRegexForTableRows(List<String> definedVariables) {
        String variableRegex = String.join(Constant.OR, definedVariables);
        String regex = CommonUtils.format(ContentConstant.REGEX_TO_DETECT_TABLE, variableRegex);
        log.info("Using regex {} to scan table rows", regex);
        return regex;
    }
}
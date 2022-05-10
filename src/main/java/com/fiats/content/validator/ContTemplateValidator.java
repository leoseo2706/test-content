package com.fiats.content.validator;


import com.fiats.content.constant.ContentErrorCode;
import com.fiats.content.jpa.entity.ContTemplateDoc;
import com.fiats.content.jpa.repo.ContTemplateRepository;
import com.fiats.content.jpa.specs.ContTemplateSpecs;
import com.fiats.content.payload.ContTemplateDocDTO;
import com.fiats.content.payload.filter.ContTemplateFilter;
import com.fiats.exception.ValidationException;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import com.fiats.tmgcoreutils.validator.CommonValidator;
import com.neo.exception.NeoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@Slf4j
public class ContTemplateValidator extends CommonValidator {

    @Autowired
    private ContTemplateRepository contTemplateRepository;

    @Autowired
    private ContTemplateSpecs contTemplateSpecs;

    public ContTemplateDoc validateExistence(ContTemplateDocDTO contTemplateDocDto, boolean expectation) {

        Optional<ContTemplateDoc> templateDocDtoOptional = contTemplateDocDto != null && contTemplateDocDto.getId() != null
                ? contTemplateRepository.findById(contTemplateDocDto.getId())
                : contTemplateRepository.findByCode(contTemplateDocDto.getCode());

        boolean actual = templateDocDtoOptional.isPresent();

        if (expectation != actual) {
            throw new ValidationException(CommonUtils.format("ContTemplate existence {0} unexpected!",
                    contTemplateDocDto.getCode()));
        }

        return actual ? templateDocDtoOptional.get() : null;
    }

    public ContTemplateDoc validateExistence(String code, boolean expectation) {

        if (!StringUtils.hasText(code)) {
            return null;
        }

        return validateExistence(ContTemplateDocDTO.builder().code(code).build(), expectation);
    }

    public void validateCodeIsEmpty(String code) {
        if (!StringUtils.hasText(code)) {
            throw new ValidationException(CommonUtils.format(" Code cannot be empty",
                    code));
        }
    }

    public void validateCodeIsChange(String codeNew,String codeOld) {
        if (!(codeOld).equals(codeNew)) {
            throw new ValidationException(CommonUtils.format(" Code cannot change",
                    codeNew));
        }
    }

    public boolean validateExistence(ContTemplateDocDTO contTemplateDocDto) {
        ContTemplateFilter filter = null;
        if (contTemplateDocDto != null) {
            if(StringUtils.hasText(contTemplateDocDto.getCode())) {
                filter = ContTemplateFilter.builder().code(contTemplateDocDto.getCode()).build();
                ContTemplateDoc template = contTemplateRepository.findAll(contTemplateSpecs.buildValidateSpecs(filter)).stream().findAny().orElse(null);
                if (template != null && (contTemplateDocDto.getId() == null || !contTemplateDocDto.getId().equals(template.getId()))) {
                    throw new NeoException(null, ContentErrorCode.DOC_TEMPLATE_CODE_DUPLICATE,
                            CommonUtils.format("ContTemplate existence {0} unexpected!",
                                    contTemplateDocDto.getCode()));
                }
            }
        }

        return true;
    }
}

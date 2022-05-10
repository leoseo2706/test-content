package com.fiats.content.service.impl;

import com.fiats.content.jpa.entity.ContGroupDoc;
import com.fiats.content.jpa.entity.ContGroupDocHistory;
import com.fiats.content.jpa.entity.ContTemplateDoc;
import com.fiats.content.jpa.entity.ContTemplateDocVersion;
import com.fiats.content.jpa.repo.ContGroupDocRepository;
import com.fiats.content.jpa.repo.ContTemplateDocRepo;
import com.fiats.content.jpa.repo.ContTemplateDocVersionRepository;
import com.fiats.content.payload.*;
import com.fiats.content.service.ConTemplateDocVersionService;
import com.fiats.exception.NeoFiatsException;
import com.fiats.tmgcoreutils.constant.Constant;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import com.fiats.tmgjpa.entity.RecordStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContTemplateDocVersionServiceImpl implements ConTemplateDocVersionService {

    @Autowired
    private ContTemplateDocVersionRepository repository;

    @Autowired
    ContGroupDocRepository cgdRepo;

    @Autowired
    ContTemplateDocRepo contTemplateDocRepo;

    @Override
    public String findContentByTemplateIdAndActiveVersion(Long templateId, Integer activeVersion) {
        log.info("Get content by templateId and activeVersion {} {}", templateId, activeVersion);
        ContTemplateDocVersion contTemplateDocVersion = repository.findByTemplateIdAndVersion(templateId, activeVersion);
        if (contTemplateDocVersion != null) {
            return contTemplateDocVersion.getContent();
        }
        return Constant.EMPTY;
    }

    @Override
    public List<ContTemplateDocVersionDTO> findByTemplateId(List<Long> templateId) {
        List<ContTemplateDocVersion> templateDocVersions = repository.findByStatusAndTemplateIdIn(RecordStatus.ACTIVE.getStatus(), templateId);
        List<ContTemplateDocVersionDTO> contTemplateDocVersionDTOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(templateDocVersions)) {
            templateDocVersions.forEach(p -> {
                ContTemplateDocVersionDTO contTemplateDocVersionDto = new ContTemplateDocVersionDTO();
                BeanUtils.copyProperties(p, contTemplateDocVersionDto);
                contTemplateDocVersionDTOS.add(contTemplateDocVersionDto);
            });

        }
        return contTemplateDocVersionDTOS;
    }

    @Override
    public ContGroupDocDTO findLatestContent(String groupDocName) {

        log.info("Finding latest templates based on {}", groupDocName);

        Optional<ContGroupDoc> contGroupDoc = cgdRepo.findLatestTemplatesByName(groupDocName);

        if (!contGroupDoc.isPresent()) {
            return null;
        }

        ContGroupDoc cgd = contGroupDoc.get();

        // keys of Template ID and Active version
        Map<ContGroupDocKey, ContGroupDocHistory> groupContentMap = cgd.getContGroupDocHistories()
                .stream()
                .collect(Collectors.toMap(ContGroupDocHistory::buildKey,
                        Function.identity(), (o, n) -> n));

        Set<Long> templateIds = groupContentMap.keySet().stream()
                .map(ContGroupDocKey::getTemplateId).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(templateIds)) {
            return null;
        }

        List<ContTemplateDocVersion> docVersions = repository.findByTemplateIdIn(templateIds)
                .stream()
                .filter(ctdv -> {
                    ContGroupDocKey key = ContGroupDocKey.builder()
                            .templateId(ctdv.getTemplateId())
                            .version(ctdv.getVersion()).build();
                    return groupContentMap.get(key) != null;
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(docVersions)
                || docVersions.size() != cgd.getContGroupDocHistories().size()) {
            throw new NeoFiatsException(
                    CommonUtils.format("Template size of group ID {0} does not match", groupDocName));
        }

        // casting to DTO
        ContGroupDocDTO groupDocDTO = new ContGroupDocDTO();
        BeanUtils.copyProperties(contGroupDoc.get(), groupDocDTO);

        List<ContGroupDocHistoryDTO> cgdhDTOs = cgd.getContGroupDocHistories()
                .stream()
                .map(tmp -> {
                    ContGroupDocHistoryDTO cgdhDTO = new ContGroupDocHistoryDTO();
                    BeanUtils.copyProperties(tmp, cgdhDTO);
                    return cgdhDTO;
                })
                .collect(Collectors.toList());
        groupDocDTO.setContGroupDocHistories(cgdhDTOs);

        List<ContTemplateDocVersionDTO> activeContents = docVersions.stream()
                .map(ctdv -> {
                    ContTemplateDocVersionDTO dto = ContTemplateDocVersionDTO.builder().build();
                    BeanUtils.copyProperties(ctdv, dto);
                    dto.setTemplateName(ctdv.getContTemplateDoc().getName());
                    dto.setTemplateCode(ctdv.getContTemplateDoc().getCode());
                    return dto;
                }).collect(Collectors.toList());
        groupDocDTO.setContTemplateDocVersions(activeContents);

        return groupDocDTO;
    }

    @Override
    public List<ContTemplateDocDTO> findByTemplateCodes(List<String> codes) {

        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptyList();
        }

        List<ContTemplateDoc> templates = contTemplateDocRepo.findByCodeIn(codes);
        return templates.stream().map(t -> {
            ContTemplateDocDTO dto = new ContTemplateDocDTO();
            BeanUtils.copyProperties(t, dto);
            return dto;
        }).collect(Collectors.toList());
    }
}

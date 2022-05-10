package com.fiats.content.service;

import com.fiats.content.payload.ContGroupDocDTO;
import com.fiats.content.payload.ContTemplateDocDTO;
import com.fiats.content.payload.ContTemplateDocVersionDTO;

import java.util.List;

public interface ConTemplateDocVersionService {

    String findContentByTemplateIdAndActiveVersion(Long templateId, Integer activeVersion);

    List<ContTemplateDocVersionDTO> findByTemplateId(List<Long> templateId);

    ContGroupDocDTO findLatestContent(String groupDocName);

    List<ContTemplateDocDTO> findByTemplateCodes(List<String> codes);

}

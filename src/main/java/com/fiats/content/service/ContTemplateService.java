package com.fiats.content.service;


import com.fiats.content.payload.ContTemplateDocDTO;
import com.fiats.content.payload.filter.ContTemplateFilter;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;

import java.util.List;

public interface ContTemplateService {


    Object save(List<ContTemplateDocDTO> contTemplateDocDto);

    ResponseMessage filter(PagingFilterBase<ContTemplateFilter> pf);

    List<ContTemplateDocDTO> findAll();

    List<ContTemplateDocDTO> findByTemplateId(List<Long> templateId);



}

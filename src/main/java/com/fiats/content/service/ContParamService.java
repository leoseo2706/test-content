package com.fiats.content.service;


import com.fiats.content.payload.ContParamDTO;
import com.fiats.content.payload.filter.ContractParamFilter;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;

import java.util.List;

public interface ContParamService {
    Object save(ContParamDTO contParam);

    ResponseMessage findByCode(PagingFilterBase<ContractParamFilter> pf);

    List<ContParamDTO> findAllContParam();

    void clearContParamCache();

}

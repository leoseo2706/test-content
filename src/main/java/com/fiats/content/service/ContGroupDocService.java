package com.fiats.content.service;


import com.fiats.content.payload.ContGroupDocDTO;
import com.fiats.content.payload.filter.ContGroupDocFilter;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;

import java.util.List;


public interface ContGroupDocService {

    Object save(List<ContGroupDocDTO> contGroupDocDTOS);

    ResponseMessage filter(PagingFilterBase<ContGroupDocFilter> pf);

    List<ContGroupDocDTO> findAll();

    ContGroupDocDTO findByName(String code);


    Object enable(Long id);

    Object disable(Long id);



}

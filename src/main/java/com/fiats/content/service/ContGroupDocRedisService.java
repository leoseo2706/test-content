package com.fiats.content.service;

import com.fiats.content.payload.ContGroupDocRedisDTO;
import com.fiats.content.payload.filter.ContGroupDocFilter;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;

import java.util.List;

public interface ContGroupDocRedisService {

    Object delete(List<String> codes);

    Object save(ContGroupDocRedisDTO contGroupDocRedis);

    Object updateStatus(List<ContGroupDocRedisDTO> contGroupDocRedis);

    ResponseMessage filter(PagingFilterBase<ContGroupDocFilter> pf);

}

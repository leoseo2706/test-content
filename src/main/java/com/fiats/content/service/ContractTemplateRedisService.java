package com.fiats.content.service;

import com.fiats.content.payload.filter.ContTemplateFilter;
import com.fiats.content.redis.entity.ContractTemplateRedis;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;

import java.util.List;

public interface ContractTemplateRedisService {

    Object delete(List<String> codes);

    Object save(ContractTemplateRedis contractTemplateRedis);

    Object updateStatus(List<ContractTemplateRedis> contractTemplateRedis);

    ResponseMessage filter(PagingFilterBase<ContTemplateFilter> pf);

}

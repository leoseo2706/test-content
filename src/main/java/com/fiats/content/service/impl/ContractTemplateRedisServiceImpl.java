package com.fiats.content.service.impl;

import com.fiats.content.constant.DataUtil;
import com.fiats.content.payload.ContTemplateDocDTO;
import com.fiats.content.payload.filter.ContTemplateFilter;
import com.fiats.content.redis.entity.ContractTemplateRedis;
import com.fiats.content.redis.repo.ContractTemplateRedisRepo;
import com.fiats.content.service.ContractTemplateRedisService;
import com.fiats.content.utils.ContractHelper;
import com.fiats.content.validator.ContTemplateValidator;
import com.fiats.tmgcoreutils.constant.Constant;
import com.fiats.tmgcoreutils.utils.DateHelper;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ContractTemplateRedisServiceImpl implements ContractTemplateRedisService {

    @Autowired
    private ContractTemplateRedisRepo redisRepo;

    @Autowired
    private ContTemplateValidator contTemplateValidator;

    @Autowired
    @Qualifier(Constant.DEFAULT_THREAD_POOL)
    TaskExecutor executor;

    @Autowired
    ContractHelper contractHelper;

    @Override
    public Object delete(List<String> lstIndex) {
        log.info("Deleting  in redis with  index {}", lstIndex);
        List<ContractTemplateRedis> contractTemplateRedis = new ArrayList<>();
        lstIndex.forEach(p -> {
            ContractTemplateRedis entity = redisRepo.findByIndex(p);
            contractTemplateRedis.add(entity);
        });
        redisRepo.deleteAll(contractTemplateRedis);
        return Constant.SUCCESS;
    }

    @Override
    public Object save(ContractTemplateRedis contractTemplateRedis) {
        ContTemplateDocDTO dto = ContTemplateDocDTO.builder().build();
        BeanUtils.copyProperties(contractTemplateRedis, dto);
        contTemplateValidator.validateExistence(dto);

        if (contractTemplateRedis.getId() == null) {
            log.info("Inserting contractTemplateRedis details {}", contractTemplateRedis);
//            contTemplateValidator.validateExistence(contractTemplateRedis.getCode(), Constant.INACTIVE);
            contractTemplateRedis.setUpdateDate(DateHelper.nowInTimestamp().getTime());
            contractTemplateRedis.setCreatedDate(DateHelper.nowInTimestamp().getTime());
        } else {
            log.info("Updating contractTemplateRedis details {}", contractTemplateRedis);
            contractTemplateRedis.setUpdateDate(DateHelper.nowInTimestamp().getTime());
        }

        CompletableFuture.runAsync(() -> {
            // open another thread to parse modified content since the content is long and it would take awhile
            try {
                Map<String, List<String>> couponTable = ContractHelper.buildAvailableVariablesForContract();
                String modifiedContent = contractHelper.findAndReplaceTableRowsWithVelocityTemplate(
                        contractTemplateRedis.getCode(), contractTemplateRedis.getName(),
                        // delete all EOL symbols (mandatory)
                        contractTemplateRedis.getContent().replaceAll("\n", ""),
                        couponTable);
                log.info("Done checking and finding defined table rows for {} and {}",
                        contractTemplateRedis.getCode(), contractTemplateRedis.getName());
                contractTemplateRedis.setContent(modifiedContent); // no EOL in html
                redisRepo.save(contractTemplateRedis); // re-update
            } catch (Exception e) {
                log.error("Error executing thread {} to modified content for {} and {}",
                        Thread.currentThread().getName(),
                        contractTemplateRedis.getCode(), contractTemplateRedis.getName());
                log.error(e.getMessage(), e);
                redisRepo.save(contractTemplateRedis); // try saving original content
            }
        }, executor);

        return Constant.SUCCESS;

    }

    @Override
    public Object updateStatus(List<ContractTemplateRedis> lstContractTemplateRedis) {
        List<ContractTemplateRedis> contractTemplateRedis = new ArrayList<>();
        for (ContractTemplateRedis redis : lstContractTemplateRedis
        ) {
            ContractTemplateRedis entity = redisRepo.findByIndex(redis.getIndex());
            if (entity != null) {
                entity.setStatus(redis.getStatus());
                log.info("Updating status with entity {} ", entity);
                contractTemplateRedis.add(entity);
            }
        }
        redisRepo.saveAll(contractTemplateRedis);
        return Constant.SUCCESS;
    }

    @Override
    public ResponseMessage filter(PagingFilterBase<ContTemplateFilter> pf) {
        List<ContractTemplateRedis> records;
        records = redisRepo.findAll(buildFilter(pf.getFilter()));
        List<ContractTemplateRedis> page = redisRepo.findAll(buildFilter(pf.getFilter()), PageRequest.of(pf.getPaging().getPageNum(), pf.getPaging().getPageSize())).getContent();
        PageRequest.of(pf.getPaging().getPageNum(), pf.getPaging().getPageSize());
        pf.getPaging().setTotalRecords((long) records.size());
        return new ResponseMessage<>(page, pf.getPaging());
    }


    private Example<ContractTemplateRedis> buildFilter(ContTemplateFilter filter) {
        ContractTemplateRedis exampleEntity = new ContractTemplateRedis();
        ExampleMatcher matcher = ExampleMatcher.matching();

        if (StringUtils.hasText(filter.getCode())) {
            exampleEntity.setCode(filter.getCode());
            matcher = matcher.withMatcher("code",
                    ExampleMatcher.GenericPropertyMatchers.exact());
        }
        return Example.of(exampleEntity, matcher);
    }
}

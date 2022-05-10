package com.fiats.content.service.impl;

import com.fiats.content.payload.ContGroupDocDTO;
import com.fiats.content.payload.ContGroupDocRedisDTO;
import com.fiats.content.payload.filter.ContGroupDocFilter;
import com.fiats.content.redis.entity.ContGroupDocRedis;
import com.fiats.content.redis.repo.ContGroupDocRedisRepo;
import com.fiats.content.service.ContGroupDocRedisService;
import com.fiats.content.validator.ContGroupValidator;
import com.fiats.tmgcoreutils.constant.Constant;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ContGroupDocRedisServiceImpl implements ContGroupDocRedisService {

    @Autowired
    private ContGroupDocRedisRepo redisRepo;

    @Autowired
    private ContGroupValidator contGroupValidator;

    @Override
    @Transactional
    public Object delete(List<String> lstIndex) {

        log.info("Deleting  in redis with  idnex {}", lstIndex);
        List<ContGroupDocRedis> contGroupDocRedis = new ArrayList<>();
        lstIndex.forEach(p -> {
            ContGroupDocRedis entity = redisRepo.findByIndex(p);
            contGroupDocRedis.add(entity);
        });
        redisRepo.deleteAll(contGroupDocRedis);
        return Constant.SUCCESS;
    }

    @Override
    @Transactional
    public Object save(ContGroupDocRedisDTO dto) {
        ContGroupDocDTO entityDTO = ContGroupDocDTO.builder().build();
        BeanUtils.copyProperties(dto, entityDTO);
        contGroupValidator.validateExistence(entityDTO);

            if (dto.getId() == null) {
//                contGroupValidator.validateExistence(dto.getCode(), Constant.INACTIVE);
                ContGroupDocRedis entity = new ContGroupDocRedis();
                BeanUtils.copyProperties(dto, entity);
                entity.setAppliedDate(dto.getAppliedDate().getTime());
                entity.setTemplateId(dto.getTemplateId());
                redisRepo.save(entity);
            } else {
                ContGroupDocRedis entity = new ContGroupDocRedis();
                BeanUtils.copyProperties(dto, entity,
                        CommonUtils.buildIgnoreAndNullPropsArray(dto));
                entity.setAppliedDate(dto.getAppliedDate().getTime());
                entity.setTemplateId(dto.getTemplateId());
                redisRepo.save(entity);
            }
        return Constant.SUCCESS;
    }

    @Override
    @Transactional
    public Object updateStatus(List<ContGroupDocRedisDTO> contGroupDocRedis) {
        for (ContGroupDocRedisDTO redis : contGroupDocRedis
        ) {
            ContGroupDocRedis entity = redisRepo.findByIndex(redis.getIndex());
            if (entity != null) {
                entity.setStatus(redis.getStatus());
                log.info("Updating status with entity {} ", redis);
                redisRepo.save(entity);
            }
        }
        return Constant.SUCCESS;
    }

    @Override
    public ResponseMessage filter(PagingFilterBase<ContGroupDocFilter> pf) {
        List<ContGroupDocRedis> records;
        records = redisRepo.findAll(buildFilter(pf.getFilter()));
        List<ContGroupDocRedis> page = redisRepo.findAll(buildFilter(pf.getFilter()), PageRequest.of(pf.getPaging().getPageNum(), pf.getPaging().getPageSize())).getContent();
        PageRequest.of(pf.getPaging().getPageNum(), pf.getPaging().getPageSize());
        pf.getPaging().setTotalRecords((long) records.size());
        return new ResponseMessage<>(page, pf.getPaging());
    }


    //
    private Example<ContGroupDocRedis> buildFilter(ContGroupDocFilter filter) {
        ContGroupDocRedis exampleEntity = new ContGroupDocRedis();
        ExampleMatcher matcher = ExampleMatcher.matching();

        if (StringUtils.hasText(filter.getCode())) {
            exampleEntity.setCode(filter.getCode());
            matcher = matcher.withMatcher("code",
                    ExampleMatcher.GenericPropertyMatchers.exact());
        }

        if (StringUtils.hasText(filter.getName())) {
            exampleEntity.setCode(filter.getName());
            matcher = matcher.withMatcher("name",
                    ExampleMatcher.GenericPropertyMatchers.exact());
        }
        return Example.of(exampleEntity, matcher);
    }
}

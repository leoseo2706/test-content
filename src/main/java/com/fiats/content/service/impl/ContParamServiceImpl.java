package com.fiats.content.service.impl;


import com.fiats.content.jpa.entity.ContParam;
import com.fiats.content.jpa.repo.ContParamRepository;
import com.fiats.content.jpa.specs.ContParamSpecs;
import com.fiats.content.payload.ContParamDTO;
import com.fiats.content.payload.filter.ContractParamFilter;
import com.fiats.content.service.ContParamService;
import com.fiats.tmgcoreutils.constant.Constant;
import com.fiats.tmgcoreutils.utils.DateHelper;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContParamServiceImpl implements ContParamService {

    @Autowired
    private ContParamRepository contParamRepository;

    @Autowired
    private ContParamSpecs contParamSpecs;

    @Override
    @Transactional
    public Object save(ContParamDTO contParamDto) {
        Optional<ContParam> contParamOptional = contParamRepository.findById(contParamDto.getId());
        if (contParamOptional.isPresent()) {
            ContParam contParam =  new ContParam();
            BeanUtils.copyProperties(contParamDto, contParam);
            contParam.setUpdatedDate(DateHelper.nowInTimestamp());
            return contParamRepository.save(contParam);
        }
        return Constant.SUCCESS;
    }

    @Override
    public ResponseMessage findByCode(PagingFilterBase<ContractParamFilter> pf) {

        Specification<ContParam> spec = contParamSpecs.buildContParamListSpecs(pf.getFilter());
        List<ContParam> records;
        if (!pf.isPageable()) {
            records = contParamRepository.findAll(spec);
        } else {
            Page<ContParam> page = contParamRepository.findAll(spec,
                    PageRequest.of(pf.getPageNum(), pf.getPageSize()));
            records = page.getContent();
            pf.getPaging().setTotalPages(page.getTotalPages());
            pf.getPaging().setTotalRecords(page.getTotalElements());
        }

        List<ContParamDTO> results = records.parallelStream().map(c -> {
            ContParamDTO dto = ContParamDTO.builder().build();
            BeanUtils.copyProperties(c, dto);
            return dto;
        }).collect(Collectors.toList());

        return new ResponseMessage<>(results, pf.getPaging());
    }

    @Override
    @Cacheable(cacheNames = "content-core.findAllContParam", keyGenerator = "keyGenerator")
    public List<ContParamDTO> findAllContParam() {

        List<ContParam> all = contParamRepository.findAll();
        log.info("Done finding all ...");

        if (CollectionUtils.isEmpty(all)) {
            return Collections.emptyList();
        }

        return all.stream().map(param -> {
            ContParamDTO dto = new ContParamDTO();
            BeanUtils.copyProperties(param, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(cacheNames = {"content-core.findAllContParam"}, allEntries = true)
    public void clearContParamCache() {
        log.info("Clearing all cache values inside content-core.findAllContParam ...");
    }
}

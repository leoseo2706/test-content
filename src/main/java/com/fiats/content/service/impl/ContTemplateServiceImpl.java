package com.fiats.content.service.impl;

import com.fiats.content.jpa.entity.ContGroupDocHistory;
import com.fiats.content.jpa.entity.ContTemplateDoc;
import com.fiats.content.jpa.entity.ContTemplateDocHistory;
import com.fiats.content.jpa.entity.ContTemplateDocVersion;
import com.fiats.content.jpa.repo.ContGroupDocHistoryRepository;
import com.fiats.content.jpa.repo.ContTemplateDocHistoryRepository;
import com.fiats.content.jpa.repo.ContTemplateDocVersionRepository;
import com.fiats.content.jpa.repo.ContTemplateRepository;
import com.fiats.content.jpa.specs.ContTemplateSpecs;
import com.fiats.content.payload.ContTemplateDocDTO;
import com.fiats.content.payload.filter.ContTemplateFilter;
import com.fiats.content.redis.entity.ContractTemplateRedis;
import com.fiats.content.redis.repo.ContractTemplateRedisRepo;
import com.fiats.content.service.ContTemplateService;
import com.fiats.content.utils.ContractHelper;
import com.fiats.content.validator.ContTemplateValidator;
import com.fiats.exception.ValidationException;
import com.fiats.tmgcoreutils.constant.Constant;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import com.fiats.tmgcoreutils.utils.DateHelper;
import com.fiats.tmgjpa.entity.RecordStatus;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContTemplateServiceImpl implements ContTemplateService {

    @Autowired
    private ContTemplateRepository repository;

    @Autowired
    private ContTemplateDocVersionRepository contTemplateDocVersionRepository;

    @Autowired
    private ContTemplateDocHistoryRepository contTemplateDocHistoryRepository;

    @Autowired
    ContractTemplateRedisRepo contractTemplateRedisRepo;

    @Autowired
    ContTemplateValidator contTemplateValidator;

    @Autowired
    ContTemplateSpecs contTemplateSpecs;

    @Autowired
    private ContGroupDocHistoryRepository contGroupDocHistoryRepository;

    @Autowired
    ContractHelper contractHelper;

    @Override
    @Transactional
    public Object save(List<ContTemplateDocDTO> contTemplateDocDTOList) {

        List<ContTemplateDocDTO> insertList = new ArrayList<>();
        List<ContTemplateDocDTO> updateList = new ArrayList<>();
        List<String> lstIndex = new ArrayList<>();
        contTemplateDocDTOList.forEach(p -> {
            contTemplateValidator.validateExistence(p);

            if (p.getId() == null) {
                insertList.add(p);
            } else {
                updateList.add(p);
            }
            lstIndex.add(p.getIndex());
        });
        Timestamp now = DateHelper.nowInTimestamp();

        // logic for insert list
        if (!CollectionUtils.isEmpty(insertList)) {
            insert(insertList, now);
        }

        // logic for update list
        if (!CollectionUtils.isEmpty(updateList)) {
            update(updateList);
        }

//         physically delete redis records
        List<ContractTemplateRedis> redisRecords = new ArrayList<>();
        if (!CollectionUtils.isEmpty(lstIndex)) {
            lstIndex.forEach(p -> {
                ContractTemplateRedis redisEntity = contractTemplateRedisRepo.findByIndex(p);
                if (redisEntity != null) {
                    redisRecords.add(redisEntity);
                }
            });
        }
        contractTemplateRedisRepo.deleteAll(redisRecords);
        return Constant.SUCCESS;

    }

    @Transactional
    public void insert(List<ContTemplateDocDTO> insertList, Timestamp now) {
        List<String> codes = new ArrayList<>();
        List<ContTemplateDoc> contTemplateDocs = new ArrayList<>();
        List<ContTemplateDocHistory> contTemplateDocHistories = new ArrayList<>();
        Integer actionVersion = 1;

        if (!CollectionUtils.isEmpty(insertList)) {
            insertList.forEach(p -> codes.add(p.getCode()));
        }

        List<ContTemplateDoc> contGroupDocByCode = repository.findByCodeIn(codes);
        Map<String, ContTemplateDoc> tmpMapTemplate = contGroupDocByCode.parallelStream().collect(Collectors.toMap(p -> p.getCode(),
                Function.identity(), (o, n) -> n));

        List<ContTemplateDoc> finalContTemplateDocs = contTemplateDocs;
        insertList.forEach(p -> {
//            check existed
            if (tmpMapTemplate.get(p.getCode()) != null) {
                throw new ValidationException(CommonUtils.format(" Cont Template existence {0} unexpected!", p.getCode()));
            }
            ContTemplateDoc contTemplateDoc = new ContTemplateDoc();
            BeanUtils.copyProperties(p, contTemplateDoc);
            contTemplateDoc.insertExtra(now);
            contTemplateDoc.setStatus(RecordStatus.ACTIVE.getStatus());
            contTemplateDoc.setActiveVersion(actionVersion);
            finalContTemplateDocs.add(contTemplateDoc);
        });
        contTemplateDocs = repository.saveAll(contTemplateDocs);
        Map<String, ContTemplateDoc> tmpMapTemplateAfterSave = contTemplateDocs.parallelStream().collect(Collectors.toMap(p -> p.getCode(),
                Function.identity(), (o, n) -> n));

        insertList.forEach(p -> {
            Long makerId = p.getMakerId();
            Long checkerId = p.getCheckerId();
            ContTemplateDoc contTemplateDoc = tmpMapTemplateAfterSave.get(p.getCode());
            ContTemplateDocVersion contTemplateDocVersion = new ContTemplateDocVersion();

            contTemplateDocVersion.setContent(p.getContent());

            contTemplateDocVersion.setCreatedDate(DateHelper.nowInTimestamp());
            contTemplateDocVersion.setUpdatedDate(DateHelper.nowInTimestamp());
            contTemplateDocVersion.setVersion(actionVersion);
            contTemplateDocVersion.setTemplateId(contTemplateDoc.getId());
            contTemplateDocVersion.setStatus(RecordStatus.ACTIVE.getStatus());
            contTemplateDocVersion.setMakerId(makerId);
            contTemplateDocVersion.setCheckerId(checkerId);
            contTemplateDocVersion.setTemplateId(contTemplateDoc.getId());
            contTemplateDocVersion = contTemplateDocVersionRepository.save(contTemplateDocVersion);

            ContTemplateDocHistory contTemplateDocHistory = new ContTemplateDocHistory();
            contTemplateDocHistory.setTemplateId(contTemplateDoc.getId());
            contTemplateDocHistory.setAppliedDate(DateHelper.nowInTimestamp());
            contTemplateDocHistory.setMakerId(makerId);
            contTemplateDocHistory.setCreatedDate(DateHelper.nowInTimestamp());
            contTemplateDocHistory.setUpdatedDate(DateHelper.nowInTimestamp());
            contTemplateDocHistory.setCheckerId(checkerId);
            contTemplateDocHistory.setVersionId(contTemplateDocVersion.getId());
            contTemplateDocHistory.setStatus(RecordStatus.ACTIVE.getStatus());
            contTemplateDocHistories.add(contTemplateDocHistory);

        });
        contTemplateDocHistoryRepository.saveAll(contTemplateDocHistories);
    }

    @Transactional
    public void update(List<ContTemplateDocDTO> updateList) {
        List<ContTemplateDoc> contTemplateDocEntity = new ArrayList<>();
        List<ContTemplateDocVersion> contTemplateDocVersions = new ArrayList<>();
        List<ContTemplateDocHistory> contTemplateDocHistories = new ArrayList<>();
        List<ContGroupDocHistory> lstContGroupDocHistory = new ArrayList<>();
        Map<Long, ContTemplateDocDTO> tmpMap = updateList.parallelStream().collect(Collectors.toMap(p -> p.getId(),
                Function.identity(), (o, n) -> n));

        List<Long> ids = tmpMap.keySet().parallelStream().collect(Collectors.toList());
        List<ContTemplateDoc> contTemplatesExits = repository.findByIdIn(ids);
        if (CollectionUtils.isEmpty(contTemplatesExits) || contTemplatesExits.size() != updateList.size()) {
            String errIds = ids.parallelStream().map(id -> id.toString()).collect(Collectors.joining(Constant.COMMA));
            throw new ValidationException(CommonUtils.format("Some or all of the ContTemplateDoc are not existent {0}", errIds));
        }

        Map<Long, ContTemplateDoc> tmpMapVerI = contTemplatesExits.parallelStream().collect(Collectors.toMap(p -> p.getId(),
                Function.identity(), (o, n) -> n));
        for (ContTemplateDocDTO contTemplateDocDto : updateList) {
            Long makerId = contTemplateDocDto.getMakerId();
            Long checkerId = contTemplateDocDto.getCheckerId();
            ContTemplateDocVersion contTemplateDocVersion = contTemplateDocVersionRepository.findFirstByTemplateIdAndStatus(contTemplateDocDto.getId(), RecordStatus.ACTIVE.getStatus());
            Integer maxVersion = contTemplateDocVersion.getVersion();
            if (maxVersion != null) {
                maxVersion = maxVersion + 1;
            } else {
                maxVersion = 1;
            }
//            update  contTemplateDoc
            ContTemplateDoc contTemplateDoc = tmpMapVerI.get(contTemplateDocDto.getId());
            contTemplateDoc.setDescription(contTemplateDocDto.getDescription());
            contTemplateDoc.setName(contTemplateDocDto.getName());
            contTemplateDoc.setStatus(RecordStatus.ACTIVE.getStatus());
            contTemplateDoc.setCode(contTemplateDocDto.getCode());
            contTemplateDoc.setActiveVersion(maxVersion);
            contTemplateDoc.setMakerId(makerId);
            contTemplateDoc.setCheckerId(checkerId);
            contTemplateDoc.setUpdatedDate(DateHelper.nowInTimestamp());
            contTemplateDocEntity.add(contTemplateDoc);


//            inactive templateDocVersion old
            ContTemplateDocVersion templateDocVersion = contTemplateDocVersionRepository.findByTemplateIdAndStatus(contTemplateDoc.getId(), RecordStatus.ACTIVE.getStatus());
            if (templateDocVersion != null) {
                templateDocVersion.setUpdatedDate(DateHelper.nowInTimestamp());
                templateDocVersion.setStatus(RecordStatus.INACTIVE.getStatus());
                contTemplateDocVersions.add(templateDocVersion);
            }

//            new  contTemplateDocVersion
            ContTemplateDocVersion tplVersion = new ContTemplateDocVersion();
            tplVersion.setContent(contTemplateDocDto.getContent());

//        contTemplateDocVersion.insertExtra(DateHelper.nowInTimestamp());
            tplVersion.setCreatedDate(DateHelper.nowInTimestamp());
            tplVersion.setUpdatedDate(DateHelper.nowInTimestamp());
            tplVersion.setVersion(maxVersion);
            tplVersion.setMakerId(makerId);
            tplVersion.setCheckerId(checkerId);
            tplVersion.setTemplateId(contTemplateDoc.getId());
            tplVersion.setStatus(RecordStatus.ACTIVE.getStatus());
            log.info("Inserting contTemplateDocVersion details {}", tplVersion);
            tplVersion = contTemplateDocVersionRepository.save(tplVersion);

//            update version of  cont GroupDoc
            lstContGroupDocHistory = contGroupDocHistoryRepository.findByStatusAndTemplateId(RecordStatus.ACTIVE.getStatus(), contTemplateDoc.getId());
            if (!CollectionUtils.isEmpty(lstContGroupDocHistory)) {
                ContTemplateDocVersion finalTplVersion = tplVersion;
                lstContGroupDocHistory.forEach(p -> {
                    p.setVersionId(finalTplVersion.getId());
                    p.setUpdatedDate(DateHelper.nowInTimestamp());
                });
            }
//            in active contTemplateDocHistory old
            ContTemplateDocHistory contTemplateDocHistory = contTemplateDocHistoryRepository.findByTemplateIdAndVersionId(contTemplateDoc.getId(), templateDocVersion.getId());
            contTemplateDocHistory.setStatus(RecordStatus.INACTIVE.getStatus());
            contTemplateDocHistory.setUpdatedDate(DateHelper.nowInTimestamp());
            contTemplateDocHistories.add(contTemplateDocHistory);
//              new  contTemplateDocHistory
            ContTemplateDocHistory templateDocHistory = new ContTemplateDocHistory();
            templateDocHistory.setTemplateId(contTemplateDoc.getId());
            templateDocHistory.setVersionId(tplVersion.getId());
            templateDocHistory.setCreatedDate(DateHelper.nowInTimestamp());
            templateDocHistory.setUpdatedDate(DateHelper.nowInTimestamp());
            templateDocHistory.setStatus(RecordStatus.ACTIVE.getStatus());
            templateDocHistory.setMakerId(makerId);
            templateDocHistory.setAppliedDate(DateHelper.nowInTimestamp());
            templateDocHistory.setCheckerId(checkerId);
            log.info("Inserting contTemplateDocHistory details {}", contTemplateDocHistory);
            contTemplateDocHistories.add(templateDocHistory);
        }
        repository.saveAll(contTemplateDocEntity);
        contTemplateDocHistoryRepository.saveAll(contTemplateDocHistories);
        if (!CollectionUtils.isEmpty(lstContGroupDocHistory)) {
            contGroupDocHistoryRepository.saveAll(lstContGroupDocHistory);
        }
    }

    @Override
    public ResponseMessage filter(PagingFilterBase<ContTemplateFilter> pf) {

        Specification<ContTemplateDoc> spec = contTemplateSpecs.buildContTemplateListSpecs(pf.getFilter());
        List<ContTemplateDoc> records;
        if (!pf.isPageable()) {
            records = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdDate"));
        } else {
            Page<ContTemplateDoc> page = repository.findAll(spec,
                    PageRequest.of(pf.getPageNum(), pf.getPageSize(), Sort.by(Sort.Direction.DESC, "createdDate")));
            records = page.getContent();
            pf.getPaging().setTotalPages(page.getTotalPages());
            pf.getPaging().setTotalRecords(page.getTotalElements());
        }

        List<ContTemplateDocDTO> results = records.parallelStream().map(c -> {
            ContTemplateDocDTO dto = ContTemplateDocDTO.builder().build();
            BeanUtils.copyProperties(c, dto);
            return dto;
        }).collect(Collectors.toList());

        return new ResponseMessage<>(results, pf.getPaging());
    }


    @Override
    public List<ContTemplateDocDTO> findAll() {
        ContTemplateFilter filter = ContTemplateFilter.builder().build();
        Specification<ContTemplateDoc> spec = contTemplateSpecs.buildContTemplateListSpecs(filter);
        List<ContTemplateDoc> entitys = repository.findAll(spec);
        return entitys.stream()
                .map(e -> {
                    ContTemplateDocDTO dto = new ContTemplateDocDTO();
                    BeanUtils.copyProperties(e, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ContTemplateDocDTO> findByTemplateId(List<Long> templateId) {
        List<ContTemplateDoc> contTemplatesExits = repository.findByIdIn(templateId);
        List<ContTemplateDocDTO> templateDocDtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(contTemplatesExits)) {
            contTemplatesExits.forEach(p -> {
                ContTemplateDocDTO dto = new ContTemplateDocDTO();
                BeanUtils.copyProperties(p, dto);
                if (!CollectionUtils.isEmpty(p.getContGroupDocHistories())) {
                    p.getContGroupDocHistories().forEach(gp -> {
                        if (RecordStatus.ACTIVE.getStatus() == gp.getStatus()) {
                            dto.setAppliedDate(gp.getAppliedDate());
                        }
                    });
                }
                if (!CollectionUtils.isEmpty(p.getContTemplateDocVersions())) {
                    p.getContTemplateDocVersions().forEach(ctv -> {
                        if (RecordStatus.ACTIVE.getStatus() == ctv.getStatus()) {
                            dto.setContent(ctv.getContent());
                            dto.setActiveVersion(ctv.getVersion());
                        }
                    });
                }
                templateDocDtos.add(dto);
            });
        }
        return templateDocDtos;
    }


}

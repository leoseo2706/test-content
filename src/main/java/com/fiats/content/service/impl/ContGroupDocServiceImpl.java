package com.fiats.content.service.impl;

import com.fiats.content.jpa.entity.ContGroupDoc;
import com.fiats.content.jpa.entity.ContGroupDocHistory;
import com.fiats.content.jpa.entity.ContTemplateDocVersion;
import com.fiats.content.jpa.repo.ContGroupDocHistoryRepository;
import com.fiats.content.jpa.repo.ContGroupDocRepository;
import com.fiats.content.jpa.repo.ContTemplateDocVersionRepository;
import com.fiats.content.jpa.specs.ContGroupSpec;
import com.fiats.content.payload.ContGroupDocDTO;
import com.fiats.content.payload.ContGroupDocHistoryDTO;
import com.fiats.content.payload.filter.ContGroupDocFilter;
import com.fiats.content.redis.entity.ContGroupDocRedis;
import com.fiats.content.redis.repo.ContGroupDocRedisRepo;
import com.fiats.content.service.ContGroupDocService;
import com.fiats.content.validator.ContGroupValidator;
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
public class ContGroupDocServiceImpl implements ContGroupDocService {

    @Autowired
    private ContGroupDocRepository contGroupDocRepository;

    @Autowired
    private ContGroupDocHistoryRepository contGroupDocHistoryRepository;

    @Autowired
    private ContGroupDocRedisRepo contGroupDocRedisRepo;

    @Autowired
    private ContTemplateDocVersionRepository contTemplateDocVersionRepository;

    @Autowired
    ContGroupValidator contGroupValidator;

    @Autowired
    ContTemplateValidator contTemplateValidator;

    @Autowired
    private ContGroupSpec contGroupSpec;

    @Override
    @Transactional
    public Object save(List<ContGroupDocDTO> contGroupDocDTOS) {
        log.info("Saving with List<ContGroupDocDTO> {} ", contGroupDocDTOS);
        List<ContGroupDocDTO> insertList = new ArrayList<>();
        List<ContGroupDocDTO> updateList = new ArrayList<>();
        List<String> lstIndex = new ArrayList<>();
        contGroupDocDTOS.forEach(p -> {
            contGroupValidator.validateExistence(p);

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
        List<ContGroupDocRedis> redisRecords = new ArrayList<>();
        if (!CollectionUtils.isEmpty(lstIndex)) {
            lstIndex.forEach(p -> {
                ContGroupDocRedis redisEntity = contGroupDocRedisRepo.findByIndex(p);
                if (redisEntity != null) {
                    redisRecords.add(redisEntity);
                }
            });
        }
        contGroupDocRedisRepo.deleteAll(redisRecords);
        return Constant.SUCCESS;
    }


    @Transactional
    public Object insert(List<ContGroupDocDTO> insertList, Timestamp now) {
        log.info("Saving with ContGroupDocDtos {} ", insertList);
        List<String> codes = new ArrayList<>();
        List<ContGroupDoc> contGroupDocs = new ArrayList<>();
        List<ContGroupDocHistory> contGroupDocHistories = new ArrayList<>();


        if (!CollectionUtils.isEmpty(insertList)) {
            insertList.forEach(p -> codes.add(p.getCode()));
        }
        List<ContGroupDoc> contGroupDocByCode = contGroupDocRepository.findByCodeIn(codes);
        Map<String, ContGroupDoc> tmpMapContGroup = contGroupDocByCode.parallelStream().collect(Collectors.toMap(p -> p.getCode(),
                Function.identity(), (o, n) -> n));
        List<ContGroupDoc> finalContGroupDocs = contGroupDocs;

//        save contGroupDoc
        insertList.forEach(p -> {
//            check existed
            if (tmpMapContGroup.get(p.getCode()) != null) {
                throw new ValidationException(CommonUtils.format("Cont group existence {0} unexpected!", p.getCode()));
            }
            ContGroupDoc contGroupDoc = new ContGroupDoc();
            BeanUtils.copyProperties(p, contGroupDoc);
            contGroupDoc.insertExtra(now);
            contGroupDoc.setStatus(RecordStatus.ACTIVE.getStatus());
            finalContGroupDocs.add(contGroupDoc);
        });
        contGroupDocs = contGroupDocRepository.saveAll(contGroupDocs);

        Map<String, ContGroupDoc> tmpMapContGroupDocAfterAdd = contGroupDocs.parallelStream().collect(Collectors.toMap(p -> p.getCode(),
                Function.identity(), (o, n) -> n));

//         save contGroupDocHistory
        insertList.forEach(p -> {
            ContGroupDoc contGroupDoc = tmpMapContGroupDocAfterAdd.get(p.getCode());
            if (!CollectionUtils.isEmpty(p.getTemplateId())) {
                p.getTemplateId().forEach(templateId -> {
                    ContTemplateDocVersion contTemplateDocVersion = contTemplateDocVersionRepository.findFirstByTemplateIdAndStatus(templateId, RecordStatus.ACTIVE.getStatus());
                    ContGroupDocHistory contGroupDocHistory = new ContGroupDocHistory();
                    contGroupDocHistory.setUpdatedDate(DateHelper.nowInTimestamp());
                    contGroupDocHistory.setCreatedDate(DateHelper.nowInTimestamp());
                    contGroupDocHistory.setVersionId(contTemplateDocVersion.getId());
                    contGroupDocHistory.setTemplateId(templateId);
                    contGroupDocHistory.setStatus(RecordStatus.ACTIVE.getStatus());
                    contGroupDocHistory.setMakerId(contGroupDoc.getMakerId());
                    contGroupDocHistory.setCheckerId(contGroupDoc.getCheckerId());
                    contGroupDocHistory.setGroupId(contGroupDoc.getId());
                    contGroupDocHistory.setAppliedDate(new Timestamp(p.getAppliedDate().getTime()));
                    log.info("Inserting contGroupDocHistory details {}", contGroupDocHistory);
                    contGroupDocHistories.add(contGroupDocHistory);
                });
            }
        });
        contGroupDocHistoryRepository.saveAll(contGroupDocHistories);

        return Constant.SUCCESS;
    }


    @Transactional
    public Object update(List<ContGroupDocDTO> updateList) {

        log.info("Updating with List<ContGroupDocDTO> {} ", updateList);

        Map<Long, ContGroupDocDTO> tmpMap = updateList.parallelStream().collect(Collectors.toMap(p -> p.getId(),
                Function.identity(), (o, n) -> n));

        List<Long> ids = tmpMap.keySet().parallelStream().collect(Collectors.toList());
        List<ContGroupDoc> contGroupDocsExits = contGroupDocRepository.findByIdIn(ids);
        List<ContGroupDoc> lstConGroupEnity = new ArrayList<>();
        List<ContGroupDocHistory> contGroupDocHistories = new ArrayList<>();


        if (CollectionUtils.isEmpty(contGroupDocsExits) || contGroupDocsExits.size() != updateList.size()) {
            String errIds = ids.parallelStream().map(id -> id.toString()).collect(Collectors.joining(Constant.COMMA));
            throw new ValidationException(CommonUtils.format("Some or all of the ContGroupDoc are not existent {0}", errIds));
        }

        Map<Long, ContGroupDoc> tmpMapVerI = contGroupDocsExits.parallelStream().collect(Collectors.toMap(p -> p.getId(),
                Function.identity(), (o, n) -> n));
        updateList.forEach(contGroupDocDTO -> {

//            update ContGroupDoc
            Long makerId = contGroupDocDTO.getMakerId();
            Long checkerId = contGroupDocDTO.getCheckerId();
            ContGroupDoc contGroupDoc = tmpMapVerI.get(contGroupDocDTO.getId());
            BeanUtils.copyProperties(contGroupDocDTO, contGroupDoc, "id");
            contGroupDoc.setUpdatedDate(DateHelper.nowInTimestamp());
            contGroupDoc.setMakerId(makerId);
            contGroupDoc.setCheckerId(checkerId);
            contGroupDoc.setStatus(RecordStatus.ACTIVE.getStatus());
            contGroupDoc.updateExtra(new Timestamp(System.currentTimeMillis()));
            lstConGroupEnity.add(contGroupDoc);

            List<ContGroupDocHistory> lstContGroupDocHistoryActive = contGroupDocHistoryRepository.findByGroupIdAndStatus(contGroupDoc.getId(), RecordStatus.ACTIVE.getStatus());


//            check record exited
            if (!CollectionUtils.isEmpty(lstContGroupDocHistoryActive) && !CollectionUtils.isEmpty(contGroupDocDTO.getTemplateId())) {
                Map<Long, ContGroupDocHistory> tmpMapVer2 = lstContGroupDocHistoryActive.parallelStream().collect(Collectors.toMap(p -> p.getTemplateId(),
                        Function.identity(), (o, n) -> n));
                ContGroupDoc finalContGroupDoc1 = contGroupDoc;
                List<Long> templateIdIsUpdate = contGroupDocDTO.getTemplateId();
                templateIdIsUpdate.forEach(templateId -> {
                    ContGroupDocHistory contGroupDocHistory = tmpMapVer2.get(templateId);
                    if (contGroupDocHistory != null) {
                        contGroupDocHistory.setUpdatedDate(DateHelper.nowInTimestamp());
                        contGroupDocHistory.setMakerId(makerId);
                        contGroupDocHistory.setCheckerId(checkerId);
                        contGroupDocHistories.add(contGroupDocHistory);
                    } else {
                        ContTemplateDocVersion contTemplateDocVersion = contTemplateDocVersionRepository.findFirstByTemplateIdAndStatus(templateId,
                                RecordStatus.ACTIVE.getStatus());
                        ContGroupDocHistory entity = new ContGroupDocHistory();
                        entity.insertExtra(DateHelper.nowInTimestamp());
                        entity.setVersionId(contTemplateDocVersion.getId());
                        entity.setTemplateId(templateId);
                        entity.setStatus(RecordStatus.ACTIVE.getStatus());
                        entity.setMakerId(makerId);
                        entity.setCheckerId(checkerId);
                        entity.setGroupId(finalContGroupDoc1.getId());
                        entity.setAppliedDate(new Timestamp(contGroupDocDTO.getAppliedDate().getTime()));
                        log.info("Inserting contGroupDocHistory details {}", entity);
                        contGroupDocHistories.add(entity);

                    }
                });

//                check template is remove
                lstContGroupDocHistoryActive.forEach(p -> {

                    Long templateIdIsExited = p.getTemplateId();
                    if (!(templateIdIsUpdate.contains(templateIdIsExited))) {
                        p.setUpdatedDate(DateHelper.nowInTimestamp());
                        p.setStatus(RecordStatus.INACTIVE.getStatus());
                        contGroupDocHistories.add(p);
                    }
                });
            }
//            check template remove all
            if (CollectionUtils.isEmpty(contGroupDocDTO.getTemplateId()) && !CollectionUtils.isEmpty(lstContGroupDocHistoryActive)) {
                lstContGroupDocHistoryActive.forEach(p -> {
                    p.setStatus(RecordStatus.INACTIVE.getStatus());
                    p.setUpdatedDate(DateHelper.nowInTimestamp());
                });
                contGroupDocHistories.addAll(lstContGroupDocHistoryActive);
            }


        });

        if (!CollectionUtils.isEmpty(contGroupDocHistories)) {
            contGroupDocHistoryRepository.saveAll(contGroupDocHistories);
        }

        if (!CollectionUtils.isEmpty(lstConGroupEnity)) {
            contGroupDocRepository.saveAll(lstConGroupEnity);
        }
        return Constant.SUCCESS;
    }


    @Override
    public List<ContGroupDocDTO> findAll() {
        List<ContGroupDoc> entity = contGroupDocRepository.findByStatus(RecordStatus.ACTIVE.getStatus());
        if (!CollectionUtils.isEmpty(entity)) {
            return entity.parallelStream().map(c -> {
                ContGroupDocDTO dto = ContGroupDocDTO.builder().build();
                BeanUtils.copyProperties(c, dto);
                List<ContGroupDocHistoryDTO> docHistoryDtoList = new ArrayList<>();
                if (c.getContGroupDocHistories() != null && !CollectionUtils.isEmpty(c.getContGroupDocHistories())) {
                    c.getContGroupDocHistories().forEach(p -> {
                        ContGroupDocHistoryDTO contGroupDocHistoryDto = ContGroupDocHistoryDTO.builder().build();
                        BeanUtils.copyProperties(p, contGroupDocHistoryDto);
                        docHistoryDtoList.add(contGroupDocHistoryDto);
                    });
                }
                dto.setContGroupDocHistories(docHistoryDtoList);
                return dto;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public ContGroupDocDTO findByName(String name) {
        ContGroupDoc entity = contGroupDocRepository.findByName(name);
        if (entity == null) {
            return null;
        }
        ContGroupDocDTO dto = ContGroupDocDTO.builder().build();
        BeanUtils.copyProperties(entity, dto);
        List<ContGroupDocHistoryDTO> docHistoryDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(entity.getContGroupDocHistories())) {
            entity.getContGroupDocHistories().forEach(p -> {
                ContGroupDocHistoryDTO contGroupDocHistoryDto = ContGroupDocHistoryDTO.builder().build();
                BeanUtils.copyProperties(p, contGroupDocHistoryDto);
                docHistoryDtoList.add(contGroupDocHistoryDto);
            });
        }
        dto.setContGroupDocHistories(docHistoryDtoList);
        return dto;
    }

    @Override
    public ResponseMessage filter(PagingFilterBase<ContGroupDocFilter> pf) {
        Specification<ContGroupDoc> spec = contGroupSpec.buildContGroupListSpecs(pf.getFilter());
        List<ContGroupDoc> records;
        if (!pf.isPageable()) {
            records = contGroupDocRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdDate"));
        } else {
            Page<ContGroupDoc> page = contGroupDocRepository.findAll(spec,
                    PageRequest.of(pf.getPageNum(), pf.getPageSize(), Sort.by(Sort.Direction.DESC, "createdDate")));
            records = page.getContent();
            pf.getPaging().setTotalPages(page.getTotalPages());
            pf.getPaging().setTotalRecords(page.getTotalElements());
        }

        List<ContGroupDocDTO> results = records.parallelStream().map(c -> {
            ContGroupDocDTO dto = ContGroupDocDTO.builder().build();
            BeanUtils.copyProperties(c, dto);
            List<ContGroupDocHistoryDTO> docHistoryDtoList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(c.getContGroupDocHistories()) && c.getContGroupDocHistories() != null) {
                c.getContGroupDocHistories().forEach(p -> {
                    ContGroupDocHistoryDTO contGroupDocHistoryDto = ContGroupDocHistoryDTO.builder().build();
                    BeanUtils.copyProperties(p, contGroupDocHistoryDto);
                    docHistoryDtoList.add(contGroupDocHistoryDto);
                });
            }
            dto.setContGroupDocHistories(docHistoryDtoList);
            return dto;
        }).collect(Collectors.toList());
        return new ResponseMessage<>(results, pf.getPaging());
    }


    @Override
    public Object enable(Long id) {
        log.info("Enabling prod prodAgreement id {}", id);
        disableOrEnable(id, RecordStatus.ACTIVE.getStatus());
        return Constant.SUCCESS;
    }

    @Override
    public Object disable(Long id) {
        log.info("Enabling prod prodAgreement id {}", id);
        disableOrEnable(id, RecordStatus.INACTIVE.getStatus());
        return Constant.SUCCESS;
    }


    private void disableOrEnable(Long conGroupId, Integer status) {

        // find and validate
        ContGroupDoc contGroupDoc = contGroupValidator.validateExistence(ContGroupDocDTO.builder().id(conGroupId).build(), Constant.ACTIVE);
        contGroupValidator.validateModifyingStatus(contGroupDoc, status);

        // transform
        log.info("Finished validation. Updating ...");
        contGroupDoc.setStatus(status);
        contGroupDocRepository.save(contGroupDoc);

        log.info("Done updating. Returning ...");
    }
}

package com.fiats.content.service.impl;


import com.fiats.content.constant.DataUtil;
import com.fiats.content.jpa.entity.ContTemplateNotification;
import com.fiats.content.jpa.repo.ContTemplateNotificationRepository;
import com.fiats.content.jpa.specs.ContTemplateNotificationSpecs;
import com.fiats.content.payload.ContTemplateNotificationDTO;
import com.fiats.content.payload.filter.ContTemplateNotificationFilter;
import com.fiats.content.service.ContTemplateNotificationService;
import com.fiats.content.validator.ContTemplateNotificationValidator;
import com.fiats.tmgcoreutils.constant.Constant;
import com.fiats.tmgcoreutils.utils.DateHelper;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContTemplateNotificationServiceImpl implements ContTemplateNotificationService {

    @Autowired
    private ContTemplateNotificationRepository contTemplateNotificationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    ContTemplateNotificationValidator contTemplateNotificationValidator;

    @Autowired
    private ContTemplateNotificationSpecs contTemplateNotificationSpecs;

    @Override
    @Transactional
    public Object save(ContTemplateNotificationDTO contTemplateNotificationDto) {
        log.info("update notification with " + contTemplateNotificationDto);
        ContTemplateNotification updateNotification = new ContTemplateNotification();
        BeanUtils.copyProperties(contTemplateNotificationDto, updateNotification);
        updateNotification.setUpdatedDate(DateHelper.nowInTimestamp());
        Optional<ContTemplateNotification> tempNoti = contTemplateNotificationRepository.findById(contTemplateNotificationDto.getId());
        if (tempNoti.isPresent() && tempNoti.get().getCreatedDate() != null) {
            updateNotification.setCreatedDate(tempNoti.get().getCreatedDate());
        }
        contTemplateNotificationRepository.save(updateNotification);
        return Constant.SUCCESS;
    }

    @Override
    public ResponseMessage findByCode(PagingFilterBase<ContTemplateNotificationFilter> pf) {
        Specification<ContTemplateNotification> spec = contTemplateNotificationSpecs.buildPropRuleListSpecs(pf.getFilter());
        List<ContTemplateNotification> records;
        if (!pf.isPageable()) {
            records = contTemplateNotificationRepository.findAll(spec);
        } else {
            Page<ContTemplateNotification> page = contTemplateNotificationRepository.findAll(spec,
                    PageRequest.of(pf.getPageNum(), pf.getPageSize()));
            records = page.getContent();
            pf.getPaging().setTotalPages(page.getTotalPages());
            pf.getPaging().setTotalRecords(page.getTotalElements());
        }

        List<ContTemplateNotificationDTO> results = records.parallelStream().map(c -> {
            ContTemplateNotificationDTO dto = ContTemplateNotificationDTO.builder().build();
            BeanUtils.copyProperties(c, dto);
            dto.setCreateDate(c.getCreatedDate());
            return dto;
        }).collect(Collectors.toList());

        return new ResponseMessage<>(results, pf.getPaging());
    }

    @Override
    public List<String> getNameByNotiTypeAndTransType(ContTemplateNotificationFilter contTemplateNotificationFilter) {
        log.info("search ContTemplateNotification with ContTemplateNotificationFilter {}", contTemplateNotificationFilter);
        List<ContTemplateNotification> lstResult = contTemplateNotificationRepository.findAll(contTemplateNotificationSpecs.buildPropRuleListSpecs(contTemplateNotificationFilter));
        List<String> lstName = new ArrayList<>();
        for (ContTemplateNotification temp : lstResult) {
            lstName.add(temp.getName());
        }
        return lstName;
    }

    @Override
    public ContTemplateNotification getByNotiTypeAndTransTypeAndName(ContTemplateNotificationFilter contTemplateNotificationFilter) {
        log.info("search ContTemplateNotification with ContTemplateNotificationFilter {}", contTemplateNotificationFilter);
        List<ContTemplateNotification> lstResult = contTemplateNotificationRepository.findAll(contTemplateNotificationSpecs.buildPropRuleListSpecs(contTemplateNotificationFilter));
        return lstResult != null ? lstResult.get(0) : new ContTemplateNotification();
    }

    @Override
    public Object createFormNotification(ContTemplateNotificationDTO contTemplateNotificationDto) {
        return save(contTemplateNotificationDto);
    }

    @Override
    public String getContentByCode(String code) {
        StringBuilder sql = new StringBuilder("select a.content  from cont_template_notification a where code = :code ");
        Query query = em.createNativeQuery(sql.toString());
        if (!DataUtil.isNullOrEmpty(code)) {
            query.setParameter("code", code);
        }
        List<Object[]> objects = query.getResultList();
        if (!DataUtil.isNullOrEmpty(objects)) {
            for (Object[] content : objects) {
                if (content != null && content[0] != null) {
                    try {
                        Clob clob = (Clob) content[0];
                        String value = clob.getSubString(1, (int) clob.length());
                        return value;
                    } catch (Exception e) {

                    }
                }
            }
        }
        return Constant.EMPTY;
    }
}

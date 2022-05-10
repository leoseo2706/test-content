package com.fiats.content.service;


import com.fiats.content.jpa.entity.ContTemplateNotification;
import com.fiats.content.payload.ContTemplateNotificationDTO;
import com.fiats.content.payload.filter.ContTemplateNotificationFilter;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;

import java.util.List;

public interface ContTemplateNotificationService {
    Object save(ContTemplateNotificationDTO contTemplateNotificationDto);

    ResponseMessage findByCode(PagingFilterBase<ContTemplateNotificationFilter> pf);

    List<String> getNameByNotiTypeAndTransType(ContTemplateNotificationFilter contTemplateNotificationFilter);

    ContTemplateNotification getByNotiTypeAndTransTypeAndName(ContTemplateNotificationFilter contTemplateNotificationFilter);

    Object createFormNotification(ContTemplateNotificationDTO contTemplateNotificationDto);

    String getContentByCode(String code);
}

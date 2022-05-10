package com.fiats.content.controller;


import com.fiats.content.payload.ContTemplateNotificationDTO;
import com.fiats.content.payload.filter.ContTemplateNotificationFilter;
import com.fiats.content.service.ContTemplateNotificationService;
import com.fiats.tmgjpa.paging.PageRequestDTO;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/form-notification")
@Validated
public class ContTemplateNotificationController {
    @Autowired
    private ContTemplateNotificationService contTemplateNotificationService;

    @GetMapping
    public ResponseMessage search(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        ContTemplateNotificationFilter filter = ContTemplateNotificationFilter.builder().code(code).build();
        PageRequestDTO page = PageRequestDTO.builder().pageNum(offset).pageSize(pageSize).build();
        return contTemplateNotificationService.findByCode(new PagingFilterBase<>(filter, page));
    }

    @GetMapping("/noti-type")
    public ResponseMessage getNameByNotiTypeAndTransType(
            @RequestParam(name = "notiType", required = false) String notiType,
            @RequestParam(name = "transactionType", required = false) String transactionType) {
        return new ResponseMessage<>(
                contTemplateNotificationService.getNameByNotiTypeAndTransType(ContTemplateNotificationFilter.builder().notiType(notiType).transactionType(transactionType).build()));
    }

    @GetMapping("/noti-type-name")
    public ResponseMessage getByNotiTypeAndTransTypeAndName(
            @RequestParam(name = "notiType", required = false) String notiType,
            @RequestParam(name = "transactionType", required = false) String transactionType,
            @RequestParam(name = "name", required = false) String name) {
        return new ResponseMessage<>(
                contTemplateNotificationService.getByNotiTypeAndTransTypeAndName(ContTemplateNotificationFilter.builder().notiType(notiType).transactionType(transactionType).name(name).build()));
    }

    @GetMapping("/content")
    public ResponseMessage getContentByCode(
            @RequestParam(name = "code", required = false) String code) {
        return new ResponseMessage<>(contTemplateNotificationService.getContentByCode(code));
    }

    @PutMapping
    public ResponseMessage updateContTemplateNotification(@Validated(ContTemplateNotificationDTO.Insert.class) @RequestBody ContTemplateNotificationDTO contTemplateNotificationDto) {
        return new ResponseMessage<>(
                contTemplateNotificationService.save(contTemplateNotificationDto));
    }

    @PostMapping
    public ResponseMessage createFormNotification(@Validated(ContTemplateNotificationDTO.Insert.class) @RequestBody ContTemplateNotificationDTO contTemplateNotificationDto) {
        return new ResponseMessage<>(
                contTemplateNotificationService.createFormNotification(contTemplateNotificationDto));
    }
}

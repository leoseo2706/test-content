package com.fiats.content.controller;


import com.fiats.content.payload.ContTemplateDocDTO;
import com.fiats.content.payload.filter.ContTemplateFilter;
import com.fiats.content.redis.entity.ContractTemplateRedis;
import com.fiats.content.service.ContTemplateService;
import com.fiats.content.service.ConTemplateDocVersionService;
import com.fiats.content.service.ContractTemplateRedisService;
import com.fiats.tmgjpa.paging.PageRequestDTO;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/template-contracts")
@Validated
public class ContTemplateController {

    @Autowired
    private ContTemplateService templateService;

    @Autowired
    private ContractTemplateRedisService redisService;

    @Autowired
    private ConTemplateDocVersionService contemplateDocVersionService;

    @GetMapping("/approval")
    public ResponseMessage search(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        ContTemplateFilter filter = ContTemplateFilter.builder().code(code).name(name).build();
        PageRequestDTO page = PageRequestDTO.builder().pageNum(offset).pageSize(pageSize).build();
        return templateService.filter(new PagingFilterBase<>(filter, page));
    }

    @PutMapping(value = "/pending-approval/change-status", headers = "Content-Type=application/json")
    public ResponseMessage updateTemplateContractPendingApproval(@Validated(ContTemplateDocDTO.Insert.class) @RequestBody List<ContractTemplateRedis> contractTemplateRedis) {
        return new ResponseMessage<>(
                redisService.updateStatus(contractTemplateRedis));
    }


    @PostMapping(value = "/pending-approval", headers = "Content-Type=application/json")
    public ResponseMessage createTemplateContractPendingApproval(@Validated(ContTemplateDocDTO.Insert.class) @RequestBody ContractTemplateRedis contractTemplateRedis) {
        return new ResponseMessage<>(
                redisService.save(contractTemplateRedis));
    }

    @PostMapping(value = "/approval", headers = "Content-Type=application/json")
    public ResponseMessage createTemplateContractApproval(@Validated(ContTemplateDocDTO.Insert.class) @RequestBody List<ContTemplateDocDTO> contTemplateDocDto) {
        return new ResponseMessage<>(
                templateService.save(contTemplateDocDto));

    }

    @GetMapping("/pending-approval")
    public ResponseMessage searchTemplateContractWaitingForApproval(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        ContTemplateFilter filter = ContTemplateFilter.builder().code(code).build();
        PageRequestDTO page = PageRequestDTO.builder().pageNum(offset).pageSize(pageSize).build();
        return redisService.filter(new PagingFilterBase<>(filter, page));
    }


    @PostMapping(value = "/pending-approval/remove", headers = "Content-Type=application/json")
    public ResponseMessage deleteTemplateContract(@RequestBody List<String> codes) {
        return new ResponseMessage<>(
                redisService.delete(codes));
    }


    @GetMapping("")
    public ResponseMessage findAll() {
        return new ResponseMessage<>(
                templateService.findAll());
    }

    @GetMapping("/content")
    public ResponseMessage findContentByTemplateIdAndActiveVersion(
            @RequestParam(name = "ti") Long templateId,
            @RequestParam(name = "av") Integer activeVersion
    ) {
        return new ResponseMessage<>(
                contemplateDocVersionService.findContentByTemplateIdAndActiveVersion(templateId, activeVersion));
    }

    @GetMapping("/by/id")
    public ResponseMessage findByTemplateId(
            @RequestParam(name = "ids") List<Long> templateId
    ) {
        return new ResponseMessage<>(
                templateService.findByTemplateId(templateId));
    }

    @GetMapping("/details")
    public List<ContTemplateDocDTO> findByCodes(@RequestParam(value = "code") List<String> codes) {
        return contemplateDocVersionService.findByTemplateCodes(codes);
    }


}


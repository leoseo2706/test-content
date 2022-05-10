package com.fiats.content.controller;

import com.fiats.content.payload.ContGroupDocDTO;
import com.fiats.content.payload.ContGroupDocRedisDTO;
import com.fiats.content.payload.filter.ContGroupDocFilter;
import com.fiats.content.service.ConTemplateDocVersionService;
import com.fiats.content.service.ContGroupDocRedisService;
import com.fiats.content.service.ContGroupDocService;
import com.fiats.tmgjpa.paging.PageRequestDTO;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(value = "/group-contract")
@Validated
public class ContGroupDocController {

    @Autowired
    private ContGroupDocService contGroupDocService;

    @Autowired
    private ContGroupDocRedisService redisService;

    @Autowired
    private ConTemplateDocVersionService ctdvService;


    @GetMapping("/approval")
    public ResponseMessage search(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "name", required = false) String name) {
        ContGroupDocFilter filter = ContGroupDocFilter.builder().name(name).code(code).build();
        PageRequestDTO page = PageRequestDTO.builder().pageNum(offset).pageSize(pageSize).build();
        return contGroupDocService.filter(new PagingFilterBase<>(filter, page));
    }


    @PutMapping(value = "/pending-approval/change-status")
    public ResponseMessage updateGroupContractPendingApproval(@Validated(ContGroupDocRedisDTO.Insert.class) @RequestBody List<ContGroupDocRedisDTO> contGroupDocRedis) {

        return new ResponseMessage<>(
                redisService.updateStatus(contGroupDocRedis));
    }


    @PostMapping(value = "/pending-approval")
    public ResponseMessage createGroupContractPendingApproval(@Validated(ContGroupDocRedisDTO.Insert.class) @RequestBody ContGroupDocRedisDTO contGroupDocRedis) {
        return new ResponseMessage<>(
                redisService.save(contGroupDocRedis));
    }

    @PostMapping(value = "/approval")
    public ResponseMessage createGroupContractApproval(@RequestBody List<ContGroupDocDTO> contGroupDocDTOS) {
        return new ResponseMessage<>(
                contGroupDocService.save(contGroupDocDTOS));
    }

    @GetMapping("/pending-approval")
    public ResponseMessage searchGroupContractWaitingForApproval(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "pageSize", required = false) Integer pageSize) {

        ContGroupDocFilter filter = ContGroupDocFilter.builder().code(code).build();
        PageRequestDTO page = PageRequestDTO.builder().pageNum(offset).pageSize(pageSize).build();
        return redisService.filter(new PagingFilterBase<>(filter, page));
    }


    @PostMapping(value = "/pending-approval/remove")
    public ResponseMessage deleteGroupContract(@RequestBody List<String> codes) {
        return new ResponseMessage<>(
                redisService.delete(codes));

    }

    @GetMapping("/search/all")
    public ResponseMessage findAll() {
        return new ResponseMessage<>(
                contGroupDocService.findAll());
    }

    @GetMapping("/search")
    public ResponseMessage findByName(@RequestParam("name") String name) {
        return new ResponseMessage<>(
                contGroupDocService.findByName(name));
    }

    @GetMapping("/{groupDocName}")
    public ContGroupDocDTO findTemplateByGroupDocName(
            @PathVariable
            @NotEmpty(message = "Group doc name cannot be empty") String groupDocName) {
        return ctdvService.findLatestContent(groupDocName);
    }


    @PatchMapping(value = "/enable/{id}")
    public ResponseMessage enable(@Positive(message = "Id must be positive") @PathVariable(value = "id") Long id) {
        return new ResponseMessage<>(contGroupDocService.enable(id));
    }

    @PatchMapping(value = "/disable/{id}")
    public ResponseMessage disable(@Positive(message = "Id must be positive") @PathVariable(value = "id") Long id) {
        return new ResponseMessage<>(contGroupDocService.disable(id));
    }
}

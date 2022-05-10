package com.fiats.content.controller;


import com.fiats.content.payload.ContParamDTO;
import com.fiats.content.payload.filter.ContractParamFilter;
import com.fiats.content.service.ContParamService;
import com.fiats.tmgjpa.paging.PageRequestDTO;
import com.fiats.tmgjpa.paging.PagingFilterBase;
import com.fiats.tmgjpa.payload.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/contract-param")
@Validated
@Slf4j
public class ContractParamController {

    @Autowired
    private ContParamService contParamService;

    @GetMapping
    public ResponseMessage search(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        ContractParamFilter filter = ContractParamFilter.builder().code(code).build();
        PageRequestDTO page = PageRequestDTO.builder().pageNum(offset).pageSize(pageSize).build();
        return contParamService.findByCode(new PagingFilterBase<>(filter, page));
    }


    @PostMapping
    public ResponseMessage updateContractParam(@Validated(ContParamDTO.Insert.class) @RequestBody ContParamDTO contParamDto) {
        ResponseMessage res = new ResponseMessage<>(contParamService.save(contParamDto));
        try {
            // evic cache
            contParamService.clearContParamCache();
        } catch (Exception e) {
            log.error("Error evict cache after inserting/updating {}", e.getMessage(), e);
        }
        return res;
    }

    @GetMapping(value = "/all")
    public List<ContParamDTO> findAllParams() {
        return contParamService.findAllContParam();
    }
}

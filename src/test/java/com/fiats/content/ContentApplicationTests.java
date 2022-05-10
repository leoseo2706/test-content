package com.fiats.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiats.content.jpa.entity.ContTemplateDocVersion;
import com.fiats.content.jpa.repo.ContTemplateDocVersionRepository;
import com.fiats.content.service.CacheService;
import com.fiats.content.utils.ContractHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
class ContentApplicationTests {

    @Autowired
    ContractHelper contractHelper;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ContTemplateDocVersionRepository docVersionRepository;

    @Autowired
    CacheService cacheService;

//    @DisplayName("test_cacheList")
//    @Test
//    void test_cacheList() {
//        log.info("test# {}", cacheService.listAllCacheManagers());
//        cacheService.clearAllCache(cacheService.listAllCacheManagers());
//        cacheService.deleteRedisCacheKeys(cacheService.listAllCacheManagers());
//    }

    @DisplayName("test add velocity foreach")
    @Test
    void test_addVelocity() {

        List<Long> idToTest = new ArrayList<>();
        idToTest.add(1224L);
        idToTest.add(1226L);

        List<ContTemplateDocVersion> templates = docVersionRepository.findAllById(idToTest);

        int i = 0;
        for (ContTemplateDocVersion template : templates) {
            Map<String, List<String>> couponTable = ContractHelper.buildAvailableVariablesForContract();
            String modifiedContent = contractHelper.findAndReplaceTableRowsWithVelocityTemplate(template.getTemplateId().toString(),
                    template.getVersion().toString(), template.getContent(), couponTable);

            if (modifiedContent != null) {
                try (FileWriter writer = new FileWriter("/Users/leoseo/Desktop/contract_" + i + ".html");
                     BufferedWriter bw = new BufferedWriter(writer)) {
                    bw.write(modifiedContent);
                } catch (IOException e) {

                }
            }
            i++;
        }
    }

}

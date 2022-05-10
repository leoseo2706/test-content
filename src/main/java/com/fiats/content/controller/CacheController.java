package com.fiats.content.controller;

import com.fiats.content.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/cache")
@Validated
public class CacheController {

    @Autowired
    CacheService cacheService;

    @GetMapping
    public Object listAll() {
        return cacheService.listAllCacheManagers();
    }

    @DeleteMapping(value = "/local")
    public void deleteLocalCache(@RequestParam(value = "c") List<String> caches) {
        cacheService.clearLocalCaches(caches);
    }

    @DeleteMapping(value = "/redis")
    public void deleteByRedisCacheManagerPrefix(@RequestParam(value = "c") List<String> caches) {
        cacheService.deleteRedisCacheKeys(caches);
    }

    @DeleteMapping(value = "/remote")
    public void deleteCacheRemotely(
            @RequestParam(value = "ep") String endpoint,
            @RequestParam(value = "value") String cacheManagerValue) {
        cacheService.clearCacheRemotely(endpoint, cacheManagerValue);
    }

}
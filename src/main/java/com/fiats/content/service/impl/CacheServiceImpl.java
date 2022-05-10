package com.fiats.content.service.impl;

import com.fiats.content.constant.ContentConstant;
import com.fiats.content.constant.ContentErrorCode;
import com.fiats.content.service.CacheService;
import com.fiats.exception.ValidationException;
import com.fiats.tmgcoreutils.constant.Constant;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import com.fiats.tmgcoreutils.utils.ReactiveClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Autowired
    CacheManager cacheManager;

    @Value("#{'${gw.internal.endpoints}'.split(',')}")
    List<String> gatewayEndpoints;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    @Qualifier("customRedisTemplate")
    RedisTemplate redisTemplate;

    @Autowired
    @Qualifier("customWebClient")
    WebClient webClient;

    @Autowired
    @Qualifier(Constant.DEFAULT_THREAD_POOL)
    TaskExecutor executor;

    @Override
    public Map<String, Collection<String>> listAllCacheManagers() {

        // does not work since caches are allocated in multiple projects
//        return cacheManager.getCacheNames();

        // call actuator endpoints to get cache names
        List<String> cacheEndpoints = gatewayEndpoints.stream()
                .map(ep -> ep.concat(ContentConstant.CACHE_URI))
                .collect(Collectors.toList());

        Map<String, Collection<String>> allAvailableCacheKeys = new HashMap<>();
        for (String cacheEndpoint : cacheEndpoints) {
            try {
                Set<String> cacheNames = new HashSet<>();
                ResponseEntity<String> res = restTemplate.getForEntity(cacheEndpoint, String.class);
                JSONObject cacheObj = new JSONObject(res.getBody())
                        .getJSONObject("cacheManagers")
                        .getJSONObject("cacheManager") // we only have 1 cache manager in application.properties
                        .getJSONObject("caches");

                Iterator keys = cacheObj.keys();
                while (keys.hasNext()) {
                    cacheNames.add(keys.next().toString());
                }
                allAvailableCacheKeys.put(cacheEndpoint.replace(ContentConstant.CACHE_URI,
                        Constant.EMPTY), cacheNames);
            } catch (Exception e) {
                // some projects do not use cache
                log.error("Unable to get cache manager via endpoint {} with error {}",
                        cacheEndpoint, e.getMessage());
                log.error(e.getMessage(), e);
            }

        }

        return allAvailableCacheKeys;

    }

    /**
     * Control evict all caches inside 1 project (work with redis only)
     *
     * @param cacheNames
     */
    @Override
    public void deleteRedisCacheKeys(Collection<String> cacheNames) {

        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection rc = factory.getConnection();

        Set<String> keys = new HashSet<>();
        for (String cacheName : cacheNames) {
            ScanOptions options = ScanOptions.scanOptions()
                    .match(cacheName.concat(Constant.WILDCARD)).build();
            Cursor<byte[]> cursor = rc.scan(options);
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                keys.add(key);
            }
        }

        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
            log.info("Done deleting redis cache keys {}", keys);
        }
    }

    @Override
    public void clearLocalCaches(Collection<String> caches) {

        if (!CollectionUtils.isEmpty(caches)) {
            cacheManager.getCacheNames().forEach(cache -> {
                try {
                    if (caches.contains(cache)) {
                        log.info("Deleting all values inside cache manager {}", cache);
                        cacheManager.getCache(cache).clear();
                    }
                } catch (Exception e) {
                    log.error("Unable to delete cache {}", cache);
                }
            });
        }
    }

    @Override
    public void clearCacheRemotely(String endpoint, String cacheName) {

        if (!StringUtils.hasText(endpoint) || !StringUtils.hasText(cacheName)) {
            throw new ValidationException(ContentErrorCode.CACHE_EVICT_PAYLOAD_EMPTY,
                    CommonUtils.format("Empty endpoint {0} or cache name {1}", endpoint, cacheName));
        }

        // http://localhost:8080/actuator/caches/countries?cacheManager=anotherCacheManager
        log.info("Beginning making DELETE request to {}/actuator/caches/{}", endpoint, cacheName);

        ReactiveClientUtils.deleteForMono(webClient, endpoint,
                "/actuator/caches/{cache}", // only 1 cache manager till now
                executor, String.class, log, cacheName).block();
        log.info("Done deleting cache managers {}", cacheName);
    }
}
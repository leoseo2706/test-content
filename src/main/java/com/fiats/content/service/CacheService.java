package com.fiats.content.service;

import java.util.Collection;
import java.util.Map;

public interface CacheService {

    Map<String, Collection<String>> listAllCacheManagers();

    void deleteRedisCacheKeys(Collection<String> cacheNames);

    void clearLocalCaches(Collection<String> caches);

    void clearCacheRemotely(String endpoint, String cacheName);

}

package com.fiats.content.config;

import com.fiats.tmgcoreutils.constant.Constant;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

@Configuration
public class CacheConfiguration extends CachingConfigurerSupport {

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) ->
                target.getClass().getSimpleName()
                        + Constant.UNDERSCORE + method.getName()
                        + Constant.UNDERSCORE + StringUtils.arrayToDelimitedString(params, Constant.UNDERSCORE);
    }

}

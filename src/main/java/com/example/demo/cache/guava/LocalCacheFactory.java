package com.example.demo.cache.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by Lenovo on 2017/8/15.
 */
@Slf4j
public class LocalCacheFactory {
    /**
     * 是否允许空值
     */
    private static boolean allowNullValues = true;
    /**
     * 最大缓存容量
     */
    private static Long maximumSize;
    /**
     * 初始缓存容量
     */
    private static int initialCapacity;
    /**
     * 设置并发级别为8，并发级别是指可以同时写缓存的线程数
     */
    private static int concurrencyLevel;

    public static Cache<Object,Object>  CreateLocalCache(String name, Long expireAfterWrite, TimeUnit unit) {
        Cache<Object , Object> localCache = null;
        if (StringUtils.isEmpty(name) || expireAfterWrite == null || unit == null) {
            return localCache;
        }
        localCache = CacheBuilder.newBuilder()
                .recordStats()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .concurrencyLevel(concurrencyLevel)
                .expireAfterWrite(expireAfterWrite, unit)
                .removalListener(new RemovalListener<Object, Object>(){
                    @Override
                    public void onRemoval(RemovalNotification<Object, Object> notification) {
                        log.info(notification.getKey()+"移除时间："+System.currentTimeMillis());
                    }})
                .build();
        return localCache;
    }

    public static boolean isAllowNullValues() {
        return allowNullValues;
    }

    public static void setAllowNullValues(boolean allowNullValues) {
        LocalCacheFactory.allowNullValues = allowNullValues;
    }

    public static Long getMaximumSize() {
        return maximumSize;
    }

    public static void setMaximumSize(Long maximumSize) {

        LocalCacheFactory.maximumSize = maximumSize;
    }

    public static int getInitialCapacity() {
        return initialCapacity;
    }

    public static void setInitialCapacity(int initialCapacity) {
        LocalCacheFactory.initialCapacity = initialCapacity;
    }

    public static int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public static void setConcurrencyLevel(int concurrencyLevel) {
        LocalCacheFactory.concurrencyLevel = concurrencyLevel;
    }
}

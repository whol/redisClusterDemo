package com.example.demo.cache.config;

import com.example.demo.cache.guava.LocalCacheFactory;
import com.google.common.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lenovo on 2017/8/15.
 */
public class LocalCacheConfig {
    public static  Map<String,Cache<Object,Object>> LOCAL_CACHE_MAP = new ConcurrentHashMap<>();
    private static Map<String,Long> LocalCacheParameter=new HashMap<>();
    private static Map<String,String> keysRelateCache=new HashMap<>();
    public static void  addLocalCacheMap()  {
        for (Map.Entry<String,Long> entry : LocalCacheParameter.entrySet()) {
            String unit=entry.getKey().substring(0, entry.getKey().lastIndexOf("_"));
            Cache<Object,Object> cache=null;
            if ("SECONDS".equals(unit)) {
                cache= LocalCacheFactory.CreateLocalCache(entry.getKey(),entry.getValue(), TimeUnit.SECONDS);
            } else if ("MINUTES".equals(unit)){
                cache=LocalCacheFactory.CreateLocalCache(entry.getKey(),entry.getValue(),TimeUnit.MINUTES);
            } else if("HOURS".equals(unit)) {
                cache=LocalCacheFactory.CreateLocalCache(entry.getKey(),entry.getValue(),TimeUnit.HOURS);
            }
            LOCAL_CACHE_MAP.put(entry.getKey(),cache);
        }
    }
    public static boolean usingLocal() {
        return LOCAL_CACHE_MAP.size() > 0;
    }

    public static Map<String, Long> getLocalCacheParameter() {
        return LocalCacheParameter;
    }

    public static void setLocalCacheParameter(Map<String, Long> localCacheParameter) {
        LocalCacheParameter = localCacheParameter;
    }

    public static Map<String, String> getKeysRelateCache() {
        return keysRelateCache;
    }

    public static void setKeysRelateCache(Map<String, String> keysRelateCache) {
        keysRelateCache = keysRelateCache;
    }

}

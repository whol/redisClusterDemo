package com.example.demo.cache.ttl.aspect;

import com.example.demo.cache.ttl.annotation.CacheTTL;

/**
 * 缓存生存时间线程变量
 *
 * @author zhuanghd
 * @since 1.0
 */
public class TTLHolder {

    private static final ThreadLocal<CacheTTL> ttl = new ThreadLocal<CacheTTL>();

    public static void setTTL(CacheTTL cacheTTL) {
        ttl.set(cacheTTL);
    }

    public static CacheTTL getTTL() {
        return ttl.get();
    }

}

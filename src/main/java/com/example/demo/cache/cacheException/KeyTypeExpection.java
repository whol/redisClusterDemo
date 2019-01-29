package com.example.demo.cache.cacheException;

/**
 * key类型异常
 * 优先级3
 */
public class KeyTypeExpection extends CMOSCacheBaseException {
    public KeyTypeExpection(String message) {
        super(message);
    }
}

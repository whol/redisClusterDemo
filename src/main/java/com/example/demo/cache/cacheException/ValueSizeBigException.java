package com.example.demo.cache.cacheException;

/**
 * value值大小过大异常
 * 优先级3
 * @author gubaofeng
 * @Date 2017/8/14
 * @Time 16:07
 */
public class ValueSizeBigException extends CMOSCacheBaseException {
    public ValueSizeBigException(String message) {
        super(message);
    }
}

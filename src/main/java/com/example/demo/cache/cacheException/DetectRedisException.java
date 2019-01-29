package com.example.demo.cache.cacheException;

/**
 * 心跳检测异常
 * 优先级2
 * @author gubf
 * @Date 2017/9/13
 * @Time 17:12
 */
public class DetectRedisException extends CMOSCacheBaseException {
    public DetectRedisException(String message) {
        super(message);
    }
}


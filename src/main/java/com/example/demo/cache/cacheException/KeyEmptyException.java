package com.example.demo.cache.cacheException;

/**
 * key值为空异常
 * 优先级3
 * @author gubaofeng
 * @Date 2017/8/17
 * @Time 15:37
 */
public class KeyEmptyException  extends CMOSCacheBaseException {
    public KeyEmptyException(String message) {
        super(message);
    }
}

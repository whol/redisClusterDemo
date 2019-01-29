package com.example.demo.cache.cacheException;

/**
 * value值为空异常
 * 优先级3
 * @author gubaofeng
 * @Date 2017/8/14
 * @Time 16:09
 */
public class ValueEmptyException  extends CMOSCacheBaseException {
    public ValueEmptyException(String message) {
        super(message);
    }
}

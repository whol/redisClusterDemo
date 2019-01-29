package com.example.demo.cache.cacheException;

/**
 * 缓存异常基类
 *
 * @author gubaofeng
 * @Date 2017/8/14
 * @Time 15:18
 */
public class CMOSCacheBaseException extends RuntimeException {

    public CMOSCacheBaseException(CMOSCacheBaseException exception)  {
        throw exception;
    }

    public CMOSCacheBaseException() {
    }

    public CMOSCacheBaseException(String message) {
        super(message);
    }

    public CMOSCacheBaseException(String message, Exception cause) {
        super(message, cause);
    }

    public CMOSCacheBaseException(Throwable cause) {
        super(cause);
    }
}

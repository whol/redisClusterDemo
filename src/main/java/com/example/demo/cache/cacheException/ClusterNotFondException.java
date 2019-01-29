package com.example.demo.cache.cacheException;

/**
 * 集群不存在异常
 * 优先级1
 * @author zhuanghd
 * @since 1.0
 */
public class ClusterNotFondException extends CMOSCacheBaseException {
    public ClusterNotFondException(String message) {
        super(message);
    }
}

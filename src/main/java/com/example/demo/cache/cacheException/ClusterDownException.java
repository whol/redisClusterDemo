package com.example.demo.cache.cacheException;

/**
 * 集群宕机异常
 * 优先级1
 * @author gubaofeng
 * @Date 2017/8/17
 * @Time 15:49
 */
public class ClusterDownException extends CMOSCacheBaseException {
    public ClusterDownException(String message) {
        super(message);
    }
}

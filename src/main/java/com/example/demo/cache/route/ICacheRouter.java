package com.example.demo.cache.route;

import com.example.demo.cache.cacheException.ClusterNotFondException;

/**
 * 缓存路由接口
 * <p>提供给业务系统实现自定义缓存路由规则
 *
 * @author zhuanghd
 * @since 1.0
 */
public interface ICacheRouter {

    /**
     * 实现路由规则,获取集群
     *
     * @param routerParam 路由参数
     * @return 集群名称
     */
    String getCluster(Object routerParam) throws ClusterNotFondException;

}

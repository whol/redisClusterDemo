package com.example.demo.cache.route.impl;

import com.example.demo.cache.cacheException.CMOSCacheBaseException;
import com.example.demo.cache.cacheException.CacheException;
import com.example.demo.cache.cacheException.CachePropertyException;
import com.example.demo.cache.cacheException.ClusterNotFondException;
import com.example.demo.cache.redis.JedisClusterFactory;
import com.example.demo.cache.route.ICacheRouter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 根据集群名称进行缓存路由
 *
 * @author zhuanghd
 * @see ICacheRouter
 * @since 1.0
 */
@Slf4j
public class ClusterNameCacheRouterImpl implements ICacheRouter {

    /**
     * 根据集群名称进行缓存路由
     * <p>该路由规则比较简单,用户传入的已经是集群名称,路由规则判断该集群如果存在则可返回集群名称
     *
     * @param routerParam 路由参数(集群名称)
     * @return 集群名称
     * @throws CacheException
     */
    public String getCluster(Object routerParam) throws ClusterNotFondException {
        String cluster = null;
        if (routerParam instanceof String) {
            String name = (String) routerParam;
            if (JedisClusterFactory.containsJedisCluster(name)) {
                cluster = name;
                log.info("当前缓存路由为按集群名称路由, 路由参数:" + routerParam + ", 路由结果:" + cluster);
            }
        } else {
            throw new CMOSCacheBaseException(new CachePropertyException("ClusterNameCacheRouterImpl的路由参数应该为集群名称(字符串)"));
        }
        if (StringUtils.isBlank(cluster)) {
            throw new CMOSCacheBaseException(new ClusterNotFondException("根据集群名称进行缓存路由失败, 找不到集群名称为'" + routerParam + "'的集群"));
        }
        return cluster;
    }
}

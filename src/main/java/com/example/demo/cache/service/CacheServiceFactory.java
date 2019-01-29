package com.example.demo.cache.service;

import com.example.demo.cache.config.LocalCacheConfig;
import com.example.demo.cache.config.RouteConfig;
import com.example.demo.cache.redis.JedisClusterFactory;
import com.example.demo.cache.route.ICacheRouter;
import com.example.demo.cache.route.impl.ClusterNameCacheRouterImpl;
import com.example.demo.cache.route.impl.ModCacheRouterImpl;
import com.example.demo.cache.service.impl.RedisCacheServiceImpl;
import redis.clients.jedis.JedisCluster;

/**
 * 缓存服务工厂
 *
 * @author zhuanghd
 * @since 1.0
 */
public abstract class CacheServiceFactory {

    /**
     * 获取缓存服务(不进行路由,兼容以前单集群的情况,如果在多集群情况下调用则抛出异常)
     * @return 缓存服务接口
     */
    public static ICacheService getService() throws Exception {
        if (checkLocalCache()){
            return new RedisCacheServiceImpl(null, true,JedisClusterFactory.getJedisCluster());
        }
        return new RedisCacheServiceImpl(null, JedisClusterFactory.getJedisCluster());
    }

    /**
     * 获取根据自定义路由的缓存服务
     * @param routerName 路由名称（对应配置文件中的路由名称）
     * @param object 用于取模计算的值
     * @return 缓存服务接口
     */
    public static ICacheService getRoutingService(String routerName, Object object) throws Exception {
        ICacheRouter router = RouteConfig.getCacheRouter(routerName);
        String clusterName = router.getCluster(object);
        JedisCluster cluster = JedisClusterFactory.getJedisCluster(clusterName);
        if (checkLocalCache()){
            return new RedisCacheServiceImpl(clusterName, true,cluster);
        }
        return new RedisCacheServiceImpl(clusterName, cluster);
    }

    /**
     * 获取集群名称路由的缓存服务
     * @param clusterName 集群名称
     * @return 缓存服务接口
     */
    public static ICacheService getClusterNameRoutingService(String clusterName) throws Exception {
        ICacheRouter router = new ClusterNameCacheRouterImpl();
        clusterName = router.getCluster(clusterName);
        JedisCluster cluster = JedisClusterFactory.getJedisCluster(clusterName);
        if (checkLocalCache()){
            return new RedisCacheServiceImpl(clusterName, true,cluster);
        }
        return new RedisCacheServiceImpl(clusterName, cluster);
    }

    /**
     * 获取取模路由的缓存服务
     * @param value 用于取模计算的值
     * @return 缓存服务接口
     */
    public static ICacheService getModRoutingService(Integer value) throws Exception {
        ICacheRouter router = new ModCacheRouterImpl();
        String clusterName = router.getCluster(value);
        JedisCluster cluster = JedisClusterFactory.getJedisCluster(clusterName);
        if (checkLocalCache()){
            return new RedisCacheServiceImpl(clusterName, true,cluster);
        }
        return new RedisCacheServiceImpl(clusterName, cluster);
    }
    private static Boolean checkLocalCache(){
        Boolean result=false;
        if (LocalCacheConfig.usingLocal()) {
            result=true;
        }
        return result;
    }
}

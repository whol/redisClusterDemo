package com.example.demo.cache.route.impl;

import com.example.demo.cache.cacheException.CMOSCacheBaseException;
import com.example.demo.cache.cacheException.CachePropertyException;
import com.example.demo.cache.cacheException.ClusterNotFondException;
import com.example.demo.cache.redis.JedisClusterFactory;
import com.example.demo.cache.route.ICacheRouter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 根据取模进行缓存路由
 *
 * @author zhuanghd
 * @since 1.0
 */
@Slf4j
public class ModCacheRouterImpl implements ICacheRouter {

    @Override
    public String getCluster(Object routerParam) throws ClusterNotFondException {
        String cluster = null;
        if(routerParam instanceof Integer) {
            Integer value = (Integer) routerParam;
            List<String> allCluster = new ArrayList<>(JedisClusterFactory.getAllClusterNames());
            Collections.sort(allCluster);
            Integer mod = value % allCluster.size();
            cluster = allCluster.get(mod);
            log.info("当前缓存路由为取模路由, 路由参数:" + routerParam + ", 路由结果:" + cluster);
        } else {
            throw new CMOSCacheBaseException(new CachePropertyException("ModCacheRouterImpl的路由参数应该为取模除数(整数)"));
        }
        return cluster;
    }

}

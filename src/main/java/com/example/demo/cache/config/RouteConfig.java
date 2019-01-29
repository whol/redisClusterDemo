package com.example.demo.cache.config;

import com.example.demo.cache.cacheException.CMOSCacheBaseException;
import com.example.demo.cache.cacheException.CachePropertyException;
import com.example.demo.cache.redis.JedisClusterFactory;
import com.example.demo.cache.route.ICacheRouter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存路由配置
 *
 * @author zhuanghd
 * @since 1.0
 */
@Slf4j
public class RouteConfig {


    // 路由配置<路由名称,路由实现类>
    private static final Map<String, ICacheRouter> ROUTER_MAP = new ConcurrentHashMap<>();

    /**
     * 根据路由名称获取路由
     *
     * @param routerName 路由名称
     * @return 路由
     */
    public static ICacheRouter getCacheRouter(String routerName) {
        return ROUTER_MAP.get(routerName);
    }

    /**
     * 增加路由
     *
     * @param name            路由名称
     * @param routerClassName 路由类名
     */
    public static void addRouter(String name, String routerClassName) {
        Object router = null;
        try {
            Class clazz = Class.forName(routerClassName);
            router = clazz.newInstance();
        } catch (Exception e) {
            throw new CMOSCacheBaseException("初始化路由失败",e);
        }
        if (router instanceof ICacheRouter) {
            ROUTER_MAP.put(name, (ICacheRouter) router);
        } else {
            throw new CMOSCacheBaseException(new CachePropertyException("路由'" + routerClassName + "'未实现'" + ICacheRouter.class.getName() + "'接口!"));
        }

    }

    /**
     * 是否使用路由
     *
     * @return
     */
    public static boolean usingRoute() {
        return ROUTER_MAP.size() > 0 || JedisClusterFactory.getAllClusterNames().size() > 0;
    }
}

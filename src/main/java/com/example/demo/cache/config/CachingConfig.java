package com.example.demo.cache.config;

import com.example.demo.cache.cacheException.CMOSCacheBaseException;
import com.example.demo.cache.config.LocalCacheConfig;
import com.example.demo.cache.config.RouteConfig;
import com.example.demo.cache.guava.LocalCacheFactory;
import com.example.demo.cache.ha.ClusterStateDetectApi;
import com.example.demo.cache.ha.ClusterStateDetectCache;
import com.example.demo.cache.redis.JedisClusterFactory;
import com.example.demo.cache.redis.RedisCache;
import com.example.demo.cache.route.aspect.AnnotationCacheRouteAspect;
import com.example.demo.cache.service.impl.RedisCacheServiceImpl;
import com.example.demo.cache.ttl.aspect.AnnotationCacheTTLAspect;
import com.example.demo.cache.util.CacheConstants.PROPERTY_KYE;
import com.cmos.cfg.core.ConfigHelper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Order(2)
@Configuration
@EnableCaching
@PropertySource("classpath:" + com.example.demo.cache.config.CachingConfig.CONFIG_FILE)
/**
 * Spring Cache组件，集群监控组件配置
 */
public class CachingConfig {

    public static final String CONFIG_FILE = "config/redis.properties";

    private static final String CONFIG_PREFIX = "cmos.cache.";

    @Autowired
    private Environment env;

    @Bean
    public AnnotationCacheRouteAspect annotationCacheRouteAspect() {
        return new AnnotationCacheRouteAspect();
    }

    @Bean
    public AnnotationCacheTTLAspect annotationCacheTTLAspect() {
        return new AnnotationCacheTTLAspect();
    }

    /**
     * 初始化cacheManager配置
     *
     * @param redisCache
     * @return cacheManager配置
     */
    @Bean(name = "cmosCacheManager")
    public CacheManager cacheManager(Cache redisCache) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<Cache> caches = new ArrayList<Cache>();
        caches.add(redisCache);
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    /**
     * 初始化对象连接池配置
     *
     * @return 对象连接池配置
     */
    @Bean(name = "genericObjectPoolConfig")
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        Environment amberEnv = ConfigHelper.getEnvironment();
        if (amberEnv != null)
            env = amberEnv;

        long defaultMaxWaitmillis = -1;
        int defaultMaxTotal = 8;
        int defaultMinIdle = 0;
        int defaultMaxIdle = 8;
        String defaultTestOnBorrow = "true";
        String defaultTestOnReturn = "true";
        String defaultTestWhileIdle = "true";
        String defaultTestOnCreate = "true";
        long defaultMinEvictableIdleTimeMillis = 500;
        long defaultSoftMinEvictableIdleTimeMillis = 1000;
        long defaultTimeBetweenEvictionRunsMillis = 1000;
        int defaultNumTestsPerEvictionRun = 100;

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxWaitMillis(getPropertyValue(PROPERTY_KYE.MAXWAITMILLIS, Long.class, defaultMaxWaitmillis));
        genericObjectPoolConfig.setMaxTotal(getPropertyValue(PROPERTY_KYE.MAXTOTAL, Integer.class, defaultMaxTotal));
        genericObjectPoolConfig.setMinIdle(getPropertyValue(PROPERTY_KYE.MINIDLE, Integer.class, defaultMinIdle));
        genericObjectPoolConfig.setMaxIdle(getPropertyValue(PROPERTY_KYE.MAXIDLE, Integer.class, defaultMaxIdle));
        genericObjectPoolConfig.setTestOnBorrow(Boolean.parseBoolean(getPropertyValue(PROPERTY_KYE.TESTONBORROW, String.class, defaultTestOnBorrow)));
        genericObjectPoolConfig.setTestOnCreate(Boolean.parseBoolean(getPropertyValue(PROPERTY_KYE.TESTONCREATE, String.class, defaultTestOnCreate)));
        genericObjectPoolConfig.setTestWhileIdle(Boolean.parseBoolean(getPropertyValue(PROPERTY_KYE.TESTWHILEIDLE, String.class, defaultTestWhileIdle)));
        genericObjectPoolConfig.setTestOnReturn(Boolean.parseBoolean(getPropertyValue(PROPERTY_KYE.TESTONRETURN, String.class, defaultTestOnReturn)));
        genericObjectPoolConfig.setMinEvictableIdleTimeMillis(getPropertyValue(PROPERTY_KYE.MINEVICTABLEIDLETIMEMILLIS, Long.class, defaultMinEvictableIdleTimeMillis));
        genericObjectPoolConfig.setSoftMinEvictableIdleTimeMillis(getPropertyValue(PROPERTY_KYE.SOFTMINEVICTABLEIDLETIMEMILLIS, Long.class, defaultSoftMinEvictableIdleTimeMillis));
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(getPropertyValue(PROPERTY_KYE.TIMEBETWEENEVICTIONRUNSMILLIS, Long.class, defaultTimeBetweenEvictionRunsMillis));
        genericObjectPoolConfig.setNumTestsPerEvictionRun(getPropertyValue(PROPERTY_KYE.NUMTESTSPEREVICTIONRUN, Integer.class, defaultNumTestsPerEvictionRun));
        genericObjectPoolConfig.setLifo(Boolean.parseBoolean(getPropertyValue(PROPERTY_KYE.LIFO, String.class, genericObjectPoolConfig.getLifo() + "")));
        genericObjectPoolConfig.setBlockWhenExhausted(Boolean.parseBoolean(getPropertyValue(PROPERTY_KYE.BLOCKWHENEXHAUSTED, String.class, genericObjectPoolConfig.getBlockWhenExhausted() + "")));
        genericObjectPoolConfig.setJmxEnabled(Boolean.parseBoolean(getPropertyValue(PROPERTY_KYE.JMXENABLED, String.class, genericObjectPoolConfig.getJmxEnabled() + "")));
        genericObjectPoolConfig.setJmxNamePrefix(getPropertyValue(PROPERTY_KYE.JMXNAMEPREFIX, String.class, genericObjectPoolConfig.getJmxNamePrefix()));
        genericObjectPoolConfig.setJmxNameBase(getPropertyValue(PROPERTY_KYE.JMXNAMEBASE, String.class, genericObjectPoolConfig.getJmxNameBase()));
        genericObjectPoolConfig.setEvictionPolicyClassName(getPropertyValue(PROPERTY_KYE.EVICTIONPOLICYCLASSNAME, String.class, genericObjectPoolConfig.getEvictionPolicyClassName()));
        return genericObjectPoolConfig;
    }

    /**
     * 初始化redis集群对象工厂配置
     *
     * @param genericObjectPoolConfig 对象连接池配置
     * @return 缓存切面配置
     */
    @Bean(name = "jedisClusterFactory")
    public JedisClusterFactory jedisClusterFactory(GenericObjectPoolConfig genericObjectPoolConfig) {
//        env = ConfigHelper.getEnvironment();
        int defaultConnectionTimeout = 2000;
        int defaultSoTimeout = 3000;
        int defaultMaxRediretion = 12;
        JedisClusterFactory jedisClusterFactory = new JedisClusterFactory();

        jedisClusterFactory.setConnectionTimeout(getPropertyValue(PROPERTY_KYE.CONNECTIONTIMEOUT, Integer.class, defaultConnectionTimeout));
        jedisClusterFactory.setSoTimeout(getPropertyValue(PROPERTY_KYE.SOTIMEOUT, Integer.class, defaultSoTimeout));
        jedisClusterFactory.setMaxRedirections(getPropertyValue(PROPERTY_KYE.MAXREDIRECTIONS, Integer.class, defaultMaxRediretion));
        jedisClusterFactory.setGenericObjectPoolConfig(genericObjectPoolConfig);
        jedisClusterFactory.setJedisClusterNodes(parseJedisNodesFromAddress(getPropertyValue(PROPERTY_KYE.REDISADDRESS, String.class, "")));
        return jedisClusterFactory;
    }

    /**
     * 初始化缓存切面配置
     *
     * @param routeConfig
     * @param jedisClusterFactory
     * @return 缓存切面配置
     */
    @SuppressWarnings("static-access")
    @Bean(name = "redisCache")
    public Cache redisCache(RouteConfig routeConfig, JedisClusterFactory jedisClusterFactory) {
        int liveTime = 3600;
        RedisCache redisCache = new RedisCache();
        redisCache.setName("redisCache");
        redisCache.setLiveTime(getPropertyValue(PROPERTY_KYE.LIVETIME, Integer.class, liveTime));
        redisCache.setJedisClusterFactory(jedisClusterFactory);
        return redisCache;
    }

    /**
     * 初始化缓存服务Api配置
     *
     * @param routeConfig
     * @param jedisClusterFactory
     * @return 缓存APi服务配置
     */
    @SuppressWarnings("static-access")
    @Bean(name = "redisCacheService")
    public RedisCacheServiceImpl redisCacheService(RouteConfig routeConfig, JedisClusterFactory jedisClusterFactory, LocalCacheConfig localCacheConfig) {
        RedisCacheServiceImpl service = new RedisCacheServiceImpl();
        if (!routeConfig.usingRoute()) {
            service.setJedisCluster(null, jedisClusterFactory.getJedisCluster());
        }
        if (localCacheConfig.usingLocal()) {
            service.setLocalCache(true);
        }
        return service;
    }

    /**
     * 初始化Redis缓存集群（应用于cache切面）
     *
     * @param routeConfig
     * @return Redis集群监控（应用切面cache切面）线程启动配置
     */
    @Bean(name = "clusterStateDetectCache", initMethod = "detect")
    public ClusterStateDetectCache clusterStateDetectCache(RouteConfig routeConfig) {
        ClusterStateDetectCache clusterStateDetectCache = new ClusterStateDetectCache();
        clusterStateDetectCache.setRouteConfig(routeConfig);
        return clusterStateDetectCache;
    }

    /**
     * 初始化Redis缓存集群（应用于服务APi）
     *
     * @param routeConfig
     * @return Redis集群监控线程启动配置
     */
    @Bean(name = "clusterStateDetectApi", initMethod = "detect")
    public ClusterStateDetectApi clusterStateDetectApi(RouteConfig routeConfig) {
        ClusterStateDetectApi clusterStateDetectApi = new ClusterStateDetectApi();
        clusterStateDetectApi.setRouteConfig(routeConfig);
        return clusterStateDetectApi;
    }

    /**
     * 初始化缓存路由配置
     *
     * @param jedisClusterFactory jedisCluster工厂
     * @return 缓存路由配置
     * @throws IOException 读取配置文件失败时抛出
     */
    @SuppressWarnings("static-access")
    @Bean
    public RouteConfig initRouteConfig(JedisClusterFactory jedisClusterFactory) {
        Properties props = null;
        try {
            props = ConfigHelper.getProperties();
        } catch (Exception e) {
            Resource resource = new ClassPathResource(CONFIG_FILE);
            props = new Properties();
            try {
                props.load(resource.getInputStream());
            } catch (IOException e1) {
                throw new CMOSCacheBaseException("读取配置文件出错", e1);
            }
        }

        RouteConfig config = new RouteConfig();
        Set<Object> keys = props.keySet();
        for (Object key : keys) {
            String k = key.toString();
            if (k.split("[.]").length < 2) {
                continue;
            }
            String name = k.substring(k.indexOf(".") + 1);
            String value = (String) props.get(key);
            if (k.startsWith(CONFIG_PREFIX + PROPERTY_KYE.CACHEROUTER)) {
                config.addRouter(name, value);
            } else if (k.startsWith(PROPERTY_KYE.CACHEROUTER)) {
                config.addRouter(name, value);
            } else if (k.startsWith(CONFIG_PREFIX + PROPERTY_KYE.CACHECLUSTER)) {
                jedisClusterFactory.addRouterCluster(name, parseJedisNodesFromAddress(value));
            } else if (k.startsWith(PROPERTY_KYE.CACHECLUSTER)) {
                jedisClusterFactory.addRouterCluster(name, parseJedisNodesFromAddress(value));
            }
        }
        return config;
    }

    @Bean(name = "localCacheFactory")
    public LocalCacheFactory localCacheFactory() {
        LocalCacheFactory localCacheFactory = new LocalCacheFactory();
        return localCacheFactory;
    }

    /**
     * 从一整串Redis地址中解析Jedis节点
     *
     * @param address Redis地址
     *                Redis地址
     * @return Jedis节点集合
     */
    private Set<String> parseJedisNodesFromAddress(String address) {
        String[] redisAddress = address.split(",");
        Set<String> jedisClusterNodes = new HashSet<String>();
        for (String string : redisAddress) {
            if (StringUtils.isEmpty(string)) {
                continue;
            }
            jedisClusterNodes.add(string);

        }
        return jedisClusterNodes;
    }

    /**
     * 优先判断CONFIG_PREFIX前缀+属性名是否存在，不存在的话，再根据属性名去获取，不存在的话，再返回默认值
     *
     * @param propertyKey
     * @param var2
     * @param defaultvalue
     * @param <T>
     * @return
     */
    private <T> T getPropertyValue(String propertyKey, Class<T> var2, T defaultvalue) {
        T value = env.getProperty(CONFIG_PREFIX + propertyKey, var2);
        if (notValueT(value))
            value = env.getProperty(propertyKey, var2);
        if (notValueT(value))
            value = defaultvalue;
        return value;
    }

    /**
     * 判断是否为null或者为空
     *
     * @param value
     * @param <T>
     * @return
     */
    private <T> boolean notValueT(T value) {
        return StringUtils.isEmpty(value);
    }

}

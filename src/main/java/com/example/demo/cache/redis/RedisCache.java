package com.example.demo.cache.redis;

import com.example.demo.cache.cacheException.KeyTypeExpection;
import com.example.demo.cache.config.RouteConfig;
import com.example.demo.cache.ha.StatFlagForAspect;
import com.example.demo.cache.ttl.annotation.CacheTTL;
import com.example.demo.cache.ttl.aspect.TTLHolder;
import com.example.demo.cache.route.aspect.ClusterHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * redisCache缓存切面功能实现
 *
 * @author liuyan
 * @since 1.0
 */
@Slf4j
public class RedisCache implements Cache {

    private String name;

    private int liveTime;

    private JedisClusterFactory jedisClusterFactory;

    public Object getNativeCache() {
        return getJedisCluster();
    }

    public ValueWrapper get(Object key) {
        if (key == null) {
            log.info("key is null！" + "key=" + key);
            return null;
        } else if (!StatFlagForAspect.isNormal(null)) {
            log.info("Redis Cluster is down!" + "key=" + key);
            return null;
        } else {
            try {
                byte[] setKey = computeKey(key);
                byte[] value = getJedisCluster().get(setKey);
                if (value == null || value.length == 0) {
                    log.info("The value is null!" + "key=" + key);
                    return null;
                } else {
                    try {
                        return new SimpleValueWrapper(SerializationUtils.deserialize(value));
                    } catch (IllegalArgumentException e) {
                        log.info("the value is String type!" + "key=" + key+e.getMessage());
                        return new SimpleValueWrapper(new String(value));
                    }
                }
            } catch (KeyTypeExpection e) {
                log.error("the type of key is error:" + key.getClass().getName());
                return null;
            } catch (Exception e) {
                log.error("Getting the value from cache is error!" + "key=" + key, e);
                return null;
            }
        }
    }

    /**
     * 实现@CacheEvict注解
     */
    public void evict(Object key) {
        if (key == null) {
            log.info("The key is null！" + key);
        } else if (!StatFlagForAspect.isNormal(null)) {
            log.info("Redis Cluster is down!" + "key=" + key);
        } else {
            try {
                byte[] setKey = computeKey(key);
                getJedisCluster().del(setKey);
            } catch (KeyTypeExpection e) {
                log.error("the type of key is error:" + key.getClass().getName());
            } catch (Exception e) {
                log.error("Clear the cache data failed!" + "key=" + key, e);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void clear() {
        try {
            getJedisCluster().flushDB();
        } catch (Exception e) {
            log.error("Flushing Redis DB is error", e);
        }

    }

    public <T> T get(Object key, Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    /**
     * 实现@CachePut注解
     */
    public void put(Object key, Object value) {
        if (key == null || value == null) {
            log.error("The key or the value is null!" + "key=" + key + ",value=" + value);
        } else if (!StatFlagForAspect.isNormal(null)) {
            log.info("Redis Cluster is down!" + "key=" + key);
        } else {
            try {
                byte[] setKey = computeKey(key);
                byte[] setValue = convertToBytes(value);
                try {
                    int time = getLiveTimeFromTTLAnnotation(key.toString());
                    if(time > 0) {
                        getJedisCluster().setex(setKey, time, setValue);
                    } else if (liveTime > 0) {
                        getJedisCluster().setex(setKey, liveTime, setValue);
                    } else {
                        getJedisCluster().set(setKey, setValue);
                    }
                } catch (Exception e) {
                    log.error("Putting the value to cache is error!" + "key=" + key + ",value=" + value, e);
                }
            } catch (KeyTypeExpection e) {
                log.error("the type of key is error:" + key.getClass().getName());
            } catch (IllegalArgumentException e) {
                log.error(
                        "Failed to serialize object of type: " + value.getClass() + "key=" + key + "value=" + value, e);
            }
        }
    }

    public ValueWrapper putIfAbsent(Object key, Object value) {
        put(key, value);
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public long getLiveTime() {
        return liveTime;
    }

    /**
     * 设置JedisCluster工厂
     *
     * @param jedisClusterFactory
     *            JedisCluster工厂
     */
    public void setJedisClusterFactory(JedisClusterFactory jedisClusterFactory) {
        this.jedisClusterFactory = jedisClusterFactory;
    }

    public JedisCluster getJedisCluster() {
        if(RouteConfig.usingRoute()) {
            return ClusterHolder.getCluster();
        } else {
            return this.jedisClusterFactory.getJedisCluster();
        }
    }

    public void setLiveTime(int liveTime) {
        this.liveTime = liveTime;
    }

    /**
     * 从注解信息获取生存时间(单位秒)
     * @return
     */
    private int getLiveTimeFromTTLAnnotation(String key) {
        CacheTTL ttl = TTLHolder.getTTL();
        int seconds = 0;
        if(ttl != null) {
            TimeUnit unit = ttl.unit();
            seconds = (int) unit.toSeconds(ttl.time());
            if (seconds <= 0) {
                log.error("key='" + key +"',CacheTTL注解指定的时间:" + ttl.time() + ",单位:" + unit + ", 折合成生存时间为'" + seconds + "秒',低于Redis能设置的最小单位!");
            } else {
                log.debug("key='" + key +"',CacheTTL注解指定的时间:" + ttl.time() + ",单位:" + unit + ", 生存时间为:" + seconds + "秒.");
            }
        }
        return seconds;
    }

    /**
     * 计算键值key，键值key的类型必须是String或
     *
     * @param key
     *
     */
    private byte[] computeKey(Object key) {
        String finalKey = null;
        if (key instanceof byte[]) {
            return (byte[]) key;
        } else if (key instanceof String) {
            finalKey = (String) key;
        } else {
            throw new KeyTypeExpection("the key of type is error:" + key.getClass().getName());
        }
        return finalKey.getBytes();

    }

    private byte[] convertToBytes(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        } else {
            if (value instanceof String) {
                return ((String) value).getBytes();
            } else {
                return SerializationUtils.serialize(value);
            }
        }

    }

}
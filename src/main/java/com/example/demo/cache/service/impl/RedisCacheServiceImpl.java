package com.example.demo.cache.service.impl;

import com.example.demo.cache.config.LocalCacheConfig;
import com.example.demo.cache.ha.StatFlagForApi;
import com.example.demo.cache.redis.JedisClusterFactory;
import com.example.demo.cache.redis.SubRegistration;
import com.example.demo.cache.util.CheckClientUtil;
import com.example.demo.cache.cacheException.*;
import com.example.demo.cache.service.AbstractCacheService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
public class RedisCacheServiceImpl extends AbstractCacheService {

    // redis 集群名称
    private String clusterName = null;

    private Boolean localCache = false;

    @Autowired
    private JedisCluster jedisCluster;

    public RedisCacheServiceImpl() {
    }

    public RedisCacheServiceImpl(String clusterName, JedisCluster jedisCluster) {
        this.clusterName = clusterName;
        this.jedisCluster = jedisCluster;
    }

    public RedisCacheServiceImpl(String clusterName, Boolean localCache, JedisCluster jedisCluster) {
        this.clusterName = clusterName;
        this.localCache = localCache;
        this.jedisCluster = jedisCluster;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void setJedisCluster(String clusterName, JedisCluster jedisCluster) {
        this.clusterName = clusterName;
        this.jedisCluster = jedisCluster;
    }

    // 调用成功标识
    @SuppressWarnings("unused")
    private static final String IS_OK = Protocol.Keyword.OK.name();

    /**
     * 判断某个Key是否存在
     *
     * @param key
     * @return
     */
    public boolean isExist(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.exists(key.getBytes());
    }

    /**
     * 判断某个Key是否存在
     *
     * @param key
     * @return
     * @throws Exception
     */
    public boolean isExist(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.exists(key);
    }

    /**
     * 根据key从缓存中删除
     *
     * @param key
     * @return
     */
    public Long del(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.del(key.getBytes());
    }

    /**
     * 根据key从缓存中删除
     *
     * @param key
     * @return
     */
    public Long del(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.del(key);
    }

    /**
     * 从缓存中删除一个或多个key，不存在的 key 会被忽略。
     *
     * @param keys
     * @return
     * @throws CacheException
     */
    public Long del(byte[]... keys) {
        checkRedisStatus();
        return jedisCluster.del(keys);
    }

    /**
     * 从缓存中删除一个或多个key，不存在的 key 会被忽略。
     *
     * @param keys
     * @return
     * @throws CacheException
     */
    public Long del(String... keys) {
        checkRedisStatus();
        return jedisCluster.del(keys);
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间
     *
     * @param key
     * @return
     */
    public Long ttl(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.ttl(key.getBytes());
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间
     *
     * @param key
     * @return
     */
    public Long ttl(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.ttl(key);
    }

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     *
     * @param key
     * @return
     */
    public Long expire(String key, int seconds) {
        long result = 0;
        checkKeyAndRedisStatus(key);
        if (seconds >= 0) {
            result = jedisCluster.expire(key.getBytes(), seconds);
        } else {
            return result;
        }
        return result;
    }

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     *
     * @param key
     * @return
     */
    public Long expire(byte[] key, int seconds) {
        long result = 0;
        checkKeyAndRedisStatus(key);
        if (seconds >= 0) {
            result = jedisCluster.expire(key, seconds);
        } else {
            return result;
        }
        return result;
    }

    /**
     * 放入缓存,永久生效
     *
     * @param key
     * @return
     */
    public boolean setString(String key, String value) {
        boolean flag = false;
        checkKeyAndValueAndRedisStatus(key, value);
        byte[] setkey = key.getBytes();
        byte[] setvalue = value.getBytes();
        String result = jedisCluster.set(setkey, setvalue);
        if ("OK".equals(result)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 放入缓存,永久生效
     *
     * @param key
     * @return
     */
    public boolean setObject(String key, Object value) {
        boolean flag = false;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        CheckClientUtil.isEmptyOrBig(value);
        if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            byte[] setkey = key.getBytes();
            byte[] setvalue = SerializationUtils.serialize(value);
            String result = jedisCluster.set(setkey, setvalue);
            if ("OK".equals(result)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 放入缓存,永久生效
     *
     * @param key
     * @return
     */
    @Deprecated
    public boolean setByte(String key, byte[] value) {
        if (StringUtils.isEmpty(key)) {
            return false;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        }
        return setByte(key.getBytes(), value);
    }

    /**
     * 放入缓存,永久生效
     *
     * @param key
     * @return
     */
    public boolean setByte(byte[] key, byte[] value) {
        boolean flag = false;
        checkKeyAndValueAndRedisStatus(key, value);
        String result = jedisCluster.set(key, value);
        if ("OK".equals(result)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 放入缓存,设置生效时间
     *
     * @param key
     * @return
     */
    public boolean setex(String key, String value, int seconds) {
        boolean flag = false;
        checkKeyAndValueAndRedisStatus(key, value);
        if (seconds >= 0) {
            byte[] setkey = key.getBytes();
            byte[] setvalue = value.getBytes();
            String result = jedisCluster.setex(setkey, seconds, setvalue);
            if ("OK".equals(result)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 放入缓存,设置生效时间
     *
     * @param key
     * @return
     */
    public boolean setex(byte[] key, byte[] value, int seconds) {
        boolean flag = false;
        checkKeyAndValueAndRedisStatus(key, value);
        if (seconds >= 0) {
            String result = jedisCluster.setex(key, seconds, value);
            if ("OK".equals(result)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 放入缓存,设置生效时间
     *
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public boolean setexObject(String key, Object value, int seconds) {
        boolean flag = false;
        checkKeyAndValueAndRedisStatus(key, value);
        if (seconds >= 0) {
            byte[] setkey = key.getBytes();
            byte[] setvalue = SerializationUtils.serialize(value);
            String result = jedisCluster.setex(setkey, seconds, setvalue);
            if ("OK".equals(result)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 从缓存中获取信息
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        String value = null;
        checkKeyAndRedisStatus(key);
        if (localCache) {
            String cacheKey = null;
            if (key.contains(":")) {
                cacheKey = key.substring(0, key.indexOf(":",0));
            } else {
                cacheKey = key;
            }
            if (LocalCacheConfig.getKeysRelateCache().containsKey(cacheKey)) {
                try {
                    value = (String) getCallableCache(key, LocalCacheConfig.LOCAL_CACHE_MAP.get(LocalCacheConfig.getKeysRelateCache().get(cacheKey)));
                    return value;
                } catch (Exception e) {
                    if (e instanceof CacheLoader.InvalidCacheLoadException) {
                        return value;
                    }
                    throw new CMOSCacheBaseException("本地缓存操作异常", e);
                }
            }
        }
        byte[] result = jedisCluster.get(key.getBytes());
        if (result == null || result.length == 0) {
            return value;
        } else {
            value = new String(result);
        }
        return value;
    }

    /**
     * 从缓存中获取信息
     *
     * @param key
     */
    @Deprecated
    public byte[] getByte(String key) {
        checkKeyAndRedisStatus(key);
        if (localCache) {
            String cacheKey = null;
            if (key.contains(":")) {
                cacheKey = key.substring(0, key.indexOf(":",0));
            } else {
                cacheKey = key;
            }
            if (LocalCacheConfig.getKeysRelateCache().containsKey(cacheKey)) {
                try {
                    byte[] value = null;
                    value = (byte[]) getCallableCache(key.getBytes(), LocalCacheConfig.LOCAL_CACHE_MAP.get(LocalCacheConfig.getKeysRelateCache().get(cacheKey)));
                    return value;
                } catch (Exception e) {
                    if (e instanceof CacheLoader.InvalidCacheLoadException) {
                        return null;
                    }
                    throw new CMOSCacheBaseException("本地缓存操作异常", e);
                }
            }
        }
        return getByte(key.getBytes());
    }


    /**
     * 从缓存中获取信息
     *
     * @param key
     */
    public byte[] getByte(byte[] key) {
        byte[] value = null;
        String relateKey = null;
        checkKeyAndRedisStatus(key);
        String cacheKey = null;
        try {
            cacheKey = new String(key);
        } catch (Exception e) {
            throw new CMOSCacheBaseException(new KeyTypeExpection("键名类型异常"));
        }
        if (localCache) {
            if (cacheKey.contains(":")) {
                relateKey= cacheKey.substring(0, cacheKey.indexOf(":",0));
            } else{
                relateKey=cacheKey;
            }
            if (LocalCacheConfig.getKeysRelateCache().containsKey(relateKey)) {
                try {
                    value = (byte[]) getCallableCache(cacheKey.getBytes(), LocalCacheConfig.LOCAL_CACHE_MAP.get(LocalCacheConfig.getKeysRelateCache().get(relateKey)));
                    return value;
                } catch (Exception e) {
                    if (e instanceof CacheLoader.InvalidCacheLoadException) {
                        return null;
                    }
                    throw new CMOSCacheBaseException(new CacheException("本地缓存操作异常"));
                }
            }
        }
        return jedisCluster.get(key);
    }

    /**
     * 从缓存中获取信息
     *
     * @param key
     * @return
     */
    public Object getObject(String key) {
        Object value = null;
        checkKeyAndRedisStatus(key);
        if (localCache) {
            String cacheKey = null;
            if (key.contains(":")) {
                cacheKey = key.substring(0, key.indexOf(":",0));
            } else {
                cacheKey = key;
            }
            if (LocalCacheConfig.getKeysRelateCache().containsKey(cacheKey)) {
                try {
                    byte[] result = null;
                    result = (byte[]) getCallableCache(key.getBytes(), LocalCacheConfig.LOCAL_CACHE_MAP.get(LocalCacheConfig.getKeysRelateCache().get(cacheKey)));
                    value = SerializationUtils.deserialize(result);
                    return value;
                } catch (Exception e) {
                    if (e instanceof CacheLoader.InvalidCacheLoadException) {
                        return value;
                    }
                    throw new CMOSCacheBaseException("本地缓存操作异常", e);
                }
            }
        }
        byte[] result = jedisCluster.get(key.getBytes());
        if (result == null || result.length == 0) {
            return value;
        } else {
            value = SerializationUtils.deserialize(result);
        }
        return value;
    }

    /**
     * 递增序列，传参为String类型
     *
     * @param key
     * @return
     */
    public Long incr(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.incr(key.getBytes());
    }

    public Long incrBy(String key, long integer) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.incrBy(key.getBytes(), integer);
    }

    /**
     * 递增序列，传参为byte类型
     *
     * @param key
     * @return
     */
    public Long incr(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.incr(key);
    }

    public Long incrBy(byte[] key, long integer) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.incrBy(key, integer);
    }

    /**
     * 递减序列，传参为String类型
     *
     * @param key
     * @return
     */
    public Long decr(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.decr(key.getBytes());
    }

    public Long decrBy(String key, long integer) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.decrBy(key.getBytes(), integer);
    }

    /**
     * 递减序列，传参为byte类型
     *
     * @param key
     * @return
     */
    public Long decr(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.decr(key);
    }

    public Long decrBy(byte[] key, long integer) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.decrBy(key, integer);
    }

    /**
     * 返回列表 key的长度
     *
     * @param key
     * @return
     */
    public Long llen(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.llen(key);
    }

    /**
     * 返回列表 key的长度
     *
     * @param key
     * @return
     */
    public Long llen(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.llen(key.getBytes());
    }

    /**
     * 将值 value 插入到列表 key 的表头
     *
     * @param key
     * @return
     */
    public Long lpush(byte[] key, byte[]... values) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lpush(key, values);
    }

    /**
     * 将值 value 插入到列表 key 的表头
     *
     * @param key
     * @return
     */
    public Long lpush(String key, String... values) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lpush(key, values);
    }

    public Long rpush(String key, String... values) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.rpush(key, values);
    }

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定
     *
     * @param key
     * @return
     */
    public List<String> lrange(String key, long start, long end) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lrange(key, start, end);
    }

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定
     *
     * @param key
     * @return
     */
    public List<byte[]> lrange(byte[] key, long start, long end) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lrange(key, start, end);
    }

    /**
     * 返回列表 key 中，下标为 index 的元素。
     *
     * @param key
     * @return
     */
    public byte[] lindex(byte[] key, long index) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lindex(key, index);
    }

    /**
     * 返回列表 key 中，下标为 index 的元素。
     *
     * @param key
     * @return
     */
    public String lindex(String key, long index) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lindex(key, index);
    }

    /**
     * 移除并返回列表 key 的头元素。
     *
     * @param key
     * @return
     */
    public byte[] lpop(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lpop(key);
    }

    /**
     * 移除并返回列表 key 的头元素。
     *
     * @param key
     * @return
     */
    public String lpop(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lpop(key);
    }

    public Long lrem(String key, long count, String value) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lrem(key, count, value);
    }

    public Long lrem(byte[] key, long count, byte[] value) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lrem(key, count, value);
    }

    /**
     * 将Map放入缓存，ok成功
     *
     * @param key
     * @return
     */
    public String putMap(String key, Map<String, String> map) {
        String result = null;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (map == null || map.size() < 1) {
            throw new CMOSCacheBaseException(new ValueEmptyException("map不能为空"));
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.hmset(key, map);
        }
        return result;
    }

    /**
     * 将Map放入缓存，ok成功
     *
     * @param key
     * @return
     */
    public String putMap(byte[] key, Map<byte[], byte[]> map) {
        String result = null;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (map == null || map.size() < 1) {
            throw new CMOSCacheBaseException(new ValueEmptyException("map不能为空"));
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.hmset(key, map);
        }
        return result;
    }

    /**
     * 从缓存中读取map，如果存在key值，则返回Map对象，如果不存在，则返回null
     *
     * @param key
     * @return
     */
    public Map<String, String> getMap(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.hgetAll(key);
    }

    /**
     * 从缓存中读取map，如果存在key值，则返回Map对象，如果不存在，则返回null
     *
     * @param key
     * @return
     */
    public Map<byte[], byte[]> getMap(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.hgetAll(key);
    }

    /**
     * 设置指定key，field的值value
     *
     * @param key
     * @return
     */
    public Long hset(String key, String field, String value) {
        long result = 0;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (StringUtils.isEmpty(field))
            return result;
        CheckClientUtil.isEmptyOrBig(value);
        if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.hset(key, field, value);
        }
        return result;
    }

    /**
     * 设置指定key，field的值value
     *
     * @param key
     * @return
     */
    public Long hset(byte[] key, byte[] field, byte[] value) {
        long result = 0;
        if (key == null || key.length == 0)
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (field == null || field.length == 0) {
            return result;
        }
        CheckClientUtil.isEmptyOrBig(value);
        if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.hset(key, field, value);
        }
        return result;
    }

    /**
     * 设置指定key，field的值value
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hsetObject(String key, String field, Object value) {
        long result = 0;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (StringUtils.isEmpty(field))
            return result;
        CheckClientUtil.isEmptyOrBig(value);
        if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            byte[] setKey = key.getBytes();
            byte[] setField = field.getBytes();
            byte[] setValue = SerializationUtils.serialize(value);
            result = jedisCluster.hset(setKey, setField, setValue);
        }
        return result;
    }

    /**
     * 获取指定key，field的值value
     *
     * @param key
     * @return
     */
    public String hget(String key, String field) {
        String value = null;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (StringUtils.isEmpty(field)) {
            return value;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            value = jedisCluster.hget(key, field);
        }
        return value;
    }

    /**
     * 获取指定key，field的值value
     *
     * @param key
     * @return
     */
    public byte[] hget(byte[] key, byte[] field) {
        byte[] value = null;
        if (key == null || key.length == 0)
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (field == null || field.length == 0) {
            return value;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            value = jedisCluster.hget(key, field);
        }
        return value;
    }

    /**
     * 获取指定key，field的值value
     *
     * @param key
     * @param field
     * @return
     */
    public Object hgetObject(String key, String field) {
        Object value = null;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (StringUtils.isEmpty(field)) {
            return value;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            byte[] result = jedisCluster.hget(key.getBytes(), field.getBytes());
            if (result == null || result.length == 0) {
                return value;
            }
            value = SerializationUtils.deserialize(result);
        }
        return value;
    }

    /**
     * 将哈希表 key 中的域 field 的值设置为 value
     *
     * @param key
     * @return
     */
    public Long hsetnx(String key, String field, String value) {
        long result = 0;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (StringUtils.isEmpty(field)) {
            return result;
        }
        CheckClientUtil.isEmptyOrBig(value);
        if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.hsetnx(key, field, value);
        }
        return result;
    }

    /**
     * 将哈希表 key 中的域 field 的值设置为 value
     *
     * @param key
     * @return
     */
    public Long hsetnx(byte[] key, byte[] field, byte[] value) {
        long result = 0;
        if (key == null || key.length == 0)
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (field == null || field.length == 0) {
            return result;
        }
        CheckClientUtil.isEmptyOrBig(value);
        if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.hsetnx(key, field, value);
        }
        return result;
    }

    /**
     * 将哈希表 key 中的域 field 的值设置为 value
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hsetnxObject(String key, String field, Object value) {
        long result = 0;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (StringUtils.isEmpty(field)) {
            return result;
        }
        CheckClientUtil.isEmptyOrBig(value);
        if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            byte[] setvalue = SerializationUtils.serialize(value);
            result = jedisCluster.hsetnx(key.getBytes(), field.getBytes(), setvalue);
        }
        return result;
    }

    /**
     * 返回哈希表 key中field的数量。
     *
     * @param key
     * @return
     */
    public Long hlen(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.hlen(key);
    }

    /**
     * 返回哈希表 key中field的数量。
     *
     * @param key
     * @return
     */
    public Long hlen(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.hlen(key);
    }

    /**
     * 查看哈希表 key 中，给定域 field 是否存在
     *
     * @param key
     * @return
     */
    public Boolean hexists(String key, String field) {
        boolean flag = false;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (StringUtils.isEmpty(field)) {
            return flag;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            flag = jedisCluster.hexists(key.getBytes(), field.getBytes());
        }
        return flag;
    }

    /**
     * 查看哈希表 key 中，给定域 field 是否存在
     *
     * @param key
     * @return
     */
    public Boolean hexists(byte[] key, byte[] field) {
        boolean flag = false;
        if (key == null || key.length == 0)
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (field == null || field.length == 0) {
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            flag = jedisCluster.hexists(key, field);
        }
        return flag;
    }

    /**
     * 返回哈希表 key 中的所有域。
     *
     * @param key
     * @return
     */
    public Set<String> hkeys(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.hkeys(key);
    }

    /**
     * 返回哈希表 key 中的所有域。
     *
     * @param key
     * @return
     */
    public Set<byte[]> hkeys(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.hkeys(key);
    }

    /**
     * 返回哈希表 key 中的所有field的值
     *
     * @param key
     * @return
     */
    public List<String> hvals(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.hvals(key);
    }

    /**
     * 返回哈希表 key 中的所有field的值
     *
     * @param key
     * @return
     */
    public Collection<byte[]> hvals(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.hvals(key);
    }

    public Long hdel(String key, String... field) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.hdel(key, field);
    }

    public Long hdel(byte[] key, byte[]... field) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.hdel(key, field);
    }

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存集合的 member 元素将被忽略。
     *
     * @param key
     * @return
     */
    public Long sadd(String key, String... member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.sadd(key, member);
    }

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存集合的 member 元素将被忽略。
     *
     * @param key
     * @return
     */
    public Long sadd(byte[] key, byte[]... member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.sadd(key, member);
    }

    /**
     * 返回集合 key 的集合中元素的数量。
     *
     * @param key
     * @return
     */
    public Long scard(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.scard(key);
    }

    /**
     * 返回集合 key 的集合中元素的数量。
     *
     * @param key
     * @return
     */
    public Long scard(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.scard(key);
    }

    /**
     * 返回集合 key 中的所有成员。
     *
     * @param key
     * @return
     */
    public Set<String> smembers(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.smembers(key);
    }

    /**
     * 返回集合 key 中的所有成员。
     *
     * @param key
     * @return
     */
    public Set<byte[]> smembers(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.smembers(key);
    }

    /**
     * 操作有序的Set
     *
     * @param key
     * @return
     */
    public Long zadd(String key, double score, String member) {
        long result = 0;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (StringUtils.isEmpty(member)) {
            return result;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.zadd(key.getBytes(), score, member.getBytes());
        }
        return result;
    }

    /**
     * 操作有序的Set
     *
     * @param key
     * @return
     */
    public Long zadd(byte[] key, double score, byte[] member) {
        long result = 0;
        if (key == null || key.length == 0)
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (member == null || member.length == 0) {
            return result;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.zadd(key, score, member);
        }
        return result;
    }

    public Double zincrby(byte[] key, double score, byte[] member) {
        Double result = null;
        if (key == null || key.length == 0)
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (member == null || member.length == 0) {
            return result;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.zincrby(key, score, member);
        }
        return result;
    }


    public Double zincrby(String key, double score, String member) {
        Double result = null;
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (StringUtils.isEmpty(member)) {
            return result;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.zincrby(key.getBytes(), score, member.getBytes());
        }
        return result;
    }

    /**
     * 取分数在min和max之间的值，返回Set<String member>
     *
     * @param key
     * @return
     */
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        checkKeyAndRedisStatus(key);
        Set<String> value = null;
        if (max > min) {
            value = jedisCluster.zrevrangeByScore(key, max, min);
        }
        return value;
    }

    /**
     * 取分数在min和max之间的值，返回Set<String member>
     *
     * @param key
     * @return
     */
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        checkKeyAndRedisStatus(key);
        Set<String> value = null;
        if (max > min) {
            value = jedisCluster.zrevrangeByScore(key, max, min, offset, count);
        }
        return value;
    }

    /**
     * 取分数在min和max之间的值，返回Set<String member>
     *
     * @param key
     * @return
     */
    public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min) {
        checkKeyAndRedisStatus(key);
        Set<byte[]> value = null;
        if (max > min) {
            value = jedisCluster.zrevrangeByScore(key, max, min);
        }
        return value;
    }

    public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count) {
        checkKeyAndRedisStatus(key);
        Set<byte[]> value = null;
        if (max > min) {
            value = jedisCluster.zrevrangeByScore(key, max, min, offset, count);
        }
        return value;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        checkKeyAndRedisStatus(key);
        Set<Tuple> value = null;
        if (max > min) {
            value = jedisCluster.zrevrangeByScoreWithScores(key, max, min);
        }
        return value;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min) {
        checkKeyAndRedisStatus(key);
        Set<Tuple> value = null;
        if (max > min) {
            value = jedisCluster.zrevrangeByScoreWithScores(key, max, min);
        }
        return value;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        checkKeyAndRedisStatus(key);
        Set<Tuple> value = null;
        if (max > min) {
            value = jedisCluster.zrevrangeByScoreWithScores(key, max, min, offset, count);
        }
        return value;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min, int offset, int count) {
        checkKeyAndRedisStatus(key);
        Set<Tuple> value = null;
        if (max > min) {
            value = jedisCluster.zrevrangeByScoreWithScores(key, max, min, offset, count);
        }
        return value;
    }

    public Long zremrangeByScore(String key, double min, double max) {
        long result = 0;
        checkKeyAndRedisStatus(key);
        result = jedisCluster.zremrangeByScore(key, min, max);
        return result;
    }

    public Long zremrangeByScore(String key, String min, String max) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zremrangeByScore(key, min, max);
    }

    public Long zremrangeByScore(byte[] key, double min, double max) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zremrangeByScore(key, min, max);
    }

    public Long zremrangeByScore(byte[] key, byte[] min, byte[] max) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zremrangeByScore(key, min, max);
    }

    /**
     * 从zset中删除指定key并且value=member的值，可以一次删除多行
     *
     * @param key
     * @return
     */
    public Long zrem(String key, String... member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrem(key, member);
    }

    /**
     * 从zset中删除指定key并且value=member的值，可以一次删除多行
     *
     * @param key
     * @return
     */
    public Long zrem(byte[] key, byte[]... member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrem(key, member);
    }

    public Set<String> keys(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            return Collections.emptySet();
        } else if (pattern.equals("*")) {
            throw new CMOSCacheBaseException(new CacheException("无法使用keys *"));
        } else {
            Set<String> keys = new LinkedHashSet<String>();
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            for (String k : clusterNodes.keySet()) {
                Jedis connection = null;

                try {
                    JedisPool jp = clusterNodes.get(k);
                    connection = jp.getResource();
                    keys.addAll(connection.keys(pattern));
                } finally {
                    if (connection != null) {
                        connection.close();//用完一定要close这个链接！！！
                    }
                }
            }
            return keys;
        }
    }

    /**
     * 根据模式匹配键值，清除缓存过期键值对
     * <p><span style="color:red;">警告: 该接口被设计为用于调试,尽量避免在生产环境使用!!!</span></p>
     * <p>查找所有符合给定模式 pattern 的 key
     * <p>KEYS * 匹配缓存中所有 key
     * <p>KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * <p>KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * <p>KEYS h[ae]llo 匹配 hello 和 hallo, 但不匹配 hillo
     * <p>特殊符号用 \ 隔开
     *
     * @param pattern 表达式
     * @param seconds 键值对最长没有操作时间
     * @return
     */
    public long clearMemory(String pattern, int seconds) {
        long delnums = 0;
        if (StringUtils.isEmpty(pattern)) {
            return delnums;
        } else if (pattern.equals("*")) {
            throw new CMOSCacheBaseException(new CacheException("无法使用keys *"));
        } else {
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            for (String k : clusterNodes.keySet()) {
                Jedis connection = null;
                Set<String> keys = new LinkedHashSet<String>();
                try {
                    JedisPool jp = clusterNodes.get(k);
                    connection = jp.getResource();
                    keys.addAll(connection.keys(pattern));
                    for (String str : keys) {
                        long time = connection.objectIdletime(str);
                        if (time > seconds) {
                            connection.del(str);
                            delnums++;
                        }
                    }
                } finally {
                    if (connection != null) {
                        connection.close();// 用完一定要close这个链接！！！
                    }
                }
            }

        }
        return delnums;
    }

    public String rpop(String key) {
        String value = null;
        checkKeyAndRedisStatus(key);
        byte[] result = jedisCluster.rpop(key.getBytes());
        if (result == null || result.length == 0) {
            return value;
        } else {
            value = new String(result);
        }
        return value;

    }

    public byte[] rpop(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.rpop(key);
    }

    public boolean ltrim(String key, long start, long end) {
        boolean flag = false;
        checkKeyAndRedisStatus(key);
        String result = jedisCluster.ltrim(key, start, end);
        if ("OK".equals(result)) {
            flag = true;
        }
        return flag;

    }

    public boolean ltrim(byte[] key, long start, long end) {
        boolean flag = false;
        checkKeyAndRedisStatus(key);
        String result = jedisCluster.ltrim(key, start, end);
        if ("OK".equals(result)) {
            flag = true;
        }
        return flag;
    }

    public Long rpush(byte[] key, byte[]... values) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.rpush(key, values);
    }

    @Deprecated
    public String rpoplpush(String srckey, String dstkey) {
        String value = null;
        if (StringUtils.isEmpty(srckey) || StringUtils.isEmpty(dstkey)) {
            return value;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            value = jedisCluster.rpoplpush(srckey, dstkey);
        }
        return value;
    }

    @Deprecated
    public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
        byte[] value = null;
        if (srckey == null || srckey.length == 0 || dstkey == null || dstkey.length == 0) {
            return value;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            value = jedisCluster.rpoplpush(srckey, dstkey);
        }
        return value;
    }

    public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.linsert(key, where, pivot, value);
    }

    public Long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.linsert(key, where, pivot, value);
    }

    public Long lpushx(String key, String... values) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lpushx(key, values);
    }

    public Long lpushx(byte[] key, byte[]... values) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.lpushx(key, values);
    }

    public Long rpushx(String key, String... values) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.rpushx(key, values);
    }

    public Long rpushx(byte[] key, byte[]... values) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.rpushx(key, values);
    }

    public Double zscore(String key, String member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zscore(key, member);
    }

    public Double zscore(byte[] key, byte[] member) {
        Double result = null;
        if (key == null || key.length == 0)
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        if (member == null || member.length == 0) {
            return result;
        } else if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        } else {
            result = jedisCluster.zscore(key, member);
        }
        return result;
    }

    public Set<String> zrange(String key, long start, long end) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrange(key, start, end);
    }

    public Set<byte[]> zrange(byte[] key, long start, long end) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrange(key, start, end);
    }

    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrangeWithScores(key, start, end);
    }

    public Set<Tuple> zrangeWithScores(byte[] key, long start, long end) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrangeWithScores(key, start, end);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        checkKeyAndRedisStatus(key);
        Set<Tuple> value = null;
        if (max > min) {
            value = jedisCluster.zrangeByScoreWithScores(key, min, max);
        }
        return value;
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        checkKeyAndRedisStatus(key);
        Set<Tuple> value = null;
        if (max > min) {
            value = jedisCluster.zrangeByScoreWithScores(key, min, max, offset, count);
        }
        return value;
    }

    public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max) {
        checkKeyAndRedisStatus(key);
        Set<Tuple> value = null;
        if (max > min) {
            value = jedisCluster.zrangeByScoreWithScores(key, min, max);
        }
        return value;
    }

    public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max, int offset, int count) {
        checkKeyAndRedisStatus(key);
        Set<Tuple> value = null;
        if (max > min) {
            value = jedisCluster.zrangeByScoreWithScores(key, min, max, offset, count);
        }
        return value;
    }

    public Set<String> zrevrange(String key, long start, long end) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrevrange(key, start, end);
    }

    public Set<byte[]> zrevrange(byte[] key, long start, long end) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrevrange(key, start, end);
    }

    public Long zrank(String key, String member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrank(key, member);
    }

    public Long zrank(byte[] key, byte[] member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrank(key, member);
    }

    public Long zrevrank(String key, String member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrevrank(key, member);
    }

    public Long zrevrank(byte[] key, byte[] member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.zrevrank(key, member);
    }

    public List<String> mget(String... keys) {
        if (keys == null || keys.length == 0) {
            throw new CMOSCacheBaseException(new KeyEmptyException("keys is null!"));
        }
        checkRedisStatus();
        return jedisCluster.mget(keys);
    }

    public List<byte[]> mget(byte[]... keys) {
        if (keys == null || keys.length == 0) {
            throw new CMOSCacheBaseException(new KeyEmptyException("keys is null!"));
        }
        checkRedisStatus();
        return jedisCluster.mget(keys);
    }

    @Override
    public Long publish(String channel, String message) {
        if (channel == null || message == null) {
            return 0L;
        }
        checkRedisStatus();
        try {
            return jedisCluster.publish(channel, message);
        } catch (JedisConnectionException e) {
            return reconnect().publish(channel, message);
        }
    }

    @Override
    public Long publish(byte[] channel, byte[] message) {
        if (channel == null || message == null) {
            return 0L;
        }
        checkRedisStatus();
        try {
            return jedisCluster.publish(channel, message);
        } catch (JedisConnectionException e) {
            return reconnect().publish(channel, message);
        }
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        if (jedisPubSub == null
                || channels == null || channels.length == 0) {
            return;
        }
        checkRedisStatus();
        try {
            SubRegistration.registerChannelSub(clusterName, jedisPubSub, channels);
            jedisCluster.subscribe(jedisPubSub, channels);
        } catch (JedisConnectionException e) {
            reconnect().subscribe(jedisPubSub, channels);
        }
    }

    @Override
    public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
        if (jedisPubSub == null
                || channels == null || channels.length == 0) {
            return;
        }
        checkRedisStatus();
        try {
            SubRegistration.registerBinaryChannelSub(clusterName, jedisPubSub, channels);
            jedisCluster.subscribe(jedisPubSub, channels);
        } catch (JedisConnectionException e) {
            reconnect().subscribe(jedisPubSub, channels);
        }
    }

    @Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        if (jedisPubSub == null
                || patterns == null || patterns.length == 0) {
            return;
        }
        checkRedisStatus();
        try {
            SubRegistration.registerPatternSub(clusterName, jedisPubSub, patterns);
            jedisCluster.psubscribe(jedisPubSub, patterns);
        } catch (JedisConnectionException e) {
            reconnect().psubscribe(jedisPubSub, patterns);
        }
    }

    @Override
    public void psubscribe(BinaryJedisPubSub jedisPubSub, byte[]... patterns) {
        if (jedisPubSub == null
                || patterns == null || patterns.length == 0) {
            return;
        }
        checkRedisStatus();
        try {
            SubRegistration.registerBinaryPatternSub(clusterName, jedisPubSub, patterns);
            jedisCluster.psubscribe(jedisPubSub, patterns);
        } catch (JedisConnectionException e) {
            reconnect().psubscribe(jedisPubSub, patterns);
        }
    }

    /**
     * 集群立即重连
     */
    private JedisCluster reconnect() {
        Map map = jedisCluster.getClusterNodes();
        Set entries = map.entrySet();
        Iterator iter = entries.iterator();
        Set<HostAndPort> haps = new HashSet<HostAndPort>();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String[] arr = ((String) entry.getKey()).split(":");
            haps.add(new HostAndPort(arr[0], Integer.valueOf(arr[1])));
        }
        return new JedisCluster(haps, 2000, 18);
    }

    public String spop(String key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.spop(key);
    }

    public byte[] spop(byte[] key) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.spop(key);
    }

    public Long srem(String key, String... member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.srem(key, member);
    }

    public Long srem(byte[] key, byte[]... member) {
        checkKeyAndRedisStatus(key);
        return jedisCluster.srem(key, member);
    }


    public Long setnx(String key, String value) {
        checkKeyAndValueAndRedisStatus(key, value);
        return jedisCluster.setnx(key, value);
    }

    public Long setnx(byte[] key, byte[] value) {
        checkKeyAndValueAndRedisStatus(key, value);
        return jedisCluster.setnx(key, value);
    }

    /**
     * 判断key、value、redis状态
     *
     * @param key
     * @param value
     */
    private void checkKeyAndValueAndRedisStatus(String key, String value) {
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        CheckClientUtil.isEmptyOrBig(value);
        checkRedisStatus();
    }

    /**
     * 判断key、value、redis状态
     *
     * @param key
     * @param value
     */
    private void checkKeyAndValueAndRedisStatus(String key, Object value) {
        if (StringUtils.isEmpty(key))
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        CheckClientUtil.isEmptyOrBig(value);
        checkRedisStatus();
    }

    /**
     * 判断key、value、redis状态
     *
     * @param key
     * @param value
     */
    private void checkKeyAndValueAndRedisStatus(byte[] key, byte[] value) {
        if (key == null || key.length == 0)
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        CheckClientUtil.isEmptyOrBig(value);
        checkRedisStatus();
    }

    /**
     * 判断key、redis状态
     *
     * @param key
     */
    private void checkKeyAndRedisStatus(byte[] key) {
        if (key == null || key.length == 0)
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        checkRedisStatus();
    }

    /**
     * 判断key、redis状态
     *
     * @param key
     */
    private void checkKeyAndRedisStatus(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new CMOSCacheBaseException(new KeyEmptyException("key is null!"));
        }
        checkRedisStatus();
    }

    /**
     * 判断redis状态
     */
    private void checkRedisStatus() {
        if (!StatFlagForApi.isNormal(clusterName)) {
            throw new CMOSCacheBaseException(new ClusterDownException("Redis集群宕机"));
        }
    }

    public Boolean getLocalCache() {
        return localCache;
    }

    public void setLocalCache(Boolean localCache) {
        this.localCache = localCache;
    }

    private Object getCallableCache(final Object key, Cache<Object, Object> cache) throws ExecutionException,CMOSCacheBaseException{
        Object result = null;
        Object localKey = null;
        if (key == null) {
            return result;
        }
        if (this.clusterName == null) {
            if (key instanceof String) {
                localKey = key;
            } else if (key instanceof byte[]) {
                try {
                    localKey = new String((byte[]) key);
                } catch (Exception e) {
                    throw new CMOSCacheBaseException(new KeyTypeExpection("键名类型异常"));
                }
            }
        } else {
            if (key instanceof String) {
                localKey = this.clusterName.toUpperCase() + "_" + key;
            } else if (key instanceof byte[]) {
                try {
                    localKey = this.clusterName.toUpperCase() +"_"+new String((byte[]) key);
                } catch (Exception e) {
                    throw new CMOSCacheBaseException(new KeyTypeExpection("键名类型异常"));
                }
            }
        }
        result = cache.get(localKey, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Object value = null;
                if (key instanceof String) {
                    value = jedisCluster.get((String) key);
                } else if (key instanceof byte[]) {
                    value = jedisCluster.get((byte[]) key);
                }
                return value;
            }
        });
        return result;
    }
}

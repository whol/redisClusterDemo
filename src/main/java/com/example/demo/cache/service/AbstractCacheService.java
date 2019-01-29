package com.example.demo.cache.service;

import com.example.demo.cache.cacheException.CacheException;
import com.example.demo.cache.exception.ExecptionMessage.EXECPTION_MESSAGE;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Tuple;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractCacheService implements ICacheService {

    /**
     * 判断某个Key是否存在
     *
     * @param key
     */
    public boolean isExist(String key) {

        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);

    }


    /**
     * 判断某个Key是否存在
     *
     * @param key
     */
    public boolean isExist(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 根据key从缓存中删除
     *
     * @param key
     */
    public Long del(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }


    /**
     * 根据key从缓存中删除
     *
     * @param keys
     */
    public Long del(byte[] keys) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 从缓存中删除一个或多个key，不存在的 key 会被忽略。
     *
     * @param keys
     */
    public Long del(byte[]... keys) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 从缓存中删除一个或多个key，不存在的 key 会被忽略。
     *
     * @param key
     */
    public Long del(String... key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间
     *
     * @param key
     */
    public Long ttl(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间
     *
     * @param key
     */
    public Long ttl(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     *
     * @param key
     */
    public Long expire(String key, int seconds) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     *
     * @param key
     */
    public Long expire(byte[] key, int seconds) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 放入缓存,永久生效
     *
     * @param key
     */
    public boolean setString(String key, String value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 从缓存中获取信息
     *
     * @param key
     */
    @Deprecated
    public byte[] getByte(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 从缓存中获取信息
     *
     * @param key
     */
    public byte[] getByte(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 放入缓存,永久生效
     *
     * @param key
     */
    @Deprecated
    public boolean setByte(String key, byte[] value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 放入缓存,永久生效
     *
     * @param key
     */
    public boolean setByte(byte[] key, byte[] value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 放入缓存,永久生效
     *
     * @param key
     */
    public boolean setObject(String key, Object value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 放入缓存,设置生效时间
     *
     * @param key
     * @param value
     * @param seconds
     */
    public boolean setex(String key, String value, int seconds) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 放入缓存,设置生效时间
     *
     * @param key
     * @param value
     * @param seconds
     */
    public boolean setex(byte[] key, byte[] value, int seconds) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 放入缓存,设置生效时间
     *
     * @param key
     * @param value
     * @param seconds
     */
    public boolean setexObject(String key, Object value, int seconds) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }


    /**
     * 从缓存中获取信息
     *
     * @param key
     */
    public String getString(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 从缓存中获取信息
     *
     * @param key
     */
    public Object getObject(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }


    /**
     * 递增序列，传参为String类型
     *
     * @param key
     */
    public Long incr(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long incrBy(String key,long integer) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }
    /**
     * 递增序列，传参为byte类型
     *
     * @param key
     */
    public Long incr(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long incrBy(byte[] key,long integer) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }
    /**
     * 递减序列，传参为String类型
     *
     * @param key
     */
    public Long decr(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long decrBy(String key,long integer) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }
    /**
     * 递减序列，传参为byte类型
     *
     * @param key
     */
    public Long decr(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long decrBy(byte[] key,long integer) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回列表 key的长度
     *
     * @param key
     */
    public Long llen(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回列表 key的长度
     *
     * @param key
     */
    public Long llen(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 将值 value 插入到列表 key 的表头
     *
     * @param key
     */
    public Long lpush(byte[] key, byte[]... values) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 将值 value 插入到列表 key 的表头
     *
     * @param key
     */
    public Long lpush(String key, String... values) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long rpush(String key, String... values) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定
     *
     * @param key
     */
    public List<String> lrange(String key, long start, long end) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定
     *
     * @param key
     */
    public List<byte[]> lrange(byte[] key, long start, long end) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回列表 key 中，下标为 index 的元素。
     *
     * @param key
     */
    public byte[] lindex(byte[] key, long index) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回列表 key 中，下标为 index 的元素。
     *
     * @param key
     */
    public String lindex(String key, long index) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 移除并返回列表 key 的头元素。
     *
     * @param key
     */
    public byte[] lpop(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 移除并返回列表 key 的头元素。
     *
     * @param key
     */
    public String lpop(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long lrem(String key, long count, String value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long lrem(byte[] key, long count, byte[] value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 将Map放入缓存，ok成功
     *
     * @param key
     */
    public String putMap(String key, Map<String, String> map) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 将Map放入缓存，ok成功
     *
     * @param key
     */
    public String putMap(byte[] key, Map<byte[], byte[]> map) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 从缓存中读取map，如果存在key值，则返回Map对象，如果不存在，则返回null
     *
     * @param key
     */
    public Map<String, String> getMap(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 从缓存中读取map，如果存在key值，则返回Map对象，如果不存在，则返回null
     *
     * @param key
     */
    public Map<byte[], byte[]> getMap(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 设置指定key，field的值value
     *
     * @param key
     * @param field
     * @param value
     */
    public Long hset(String key, String field, String value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 设置指定key，field的值value
     *
     * @param key
     * @param field
     * @param value
     */
    public Long hset(byte[] key, byte[] field, byte[] value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 设置指定key，field的值value
     *
     * @param key
     * @param field
     * @param value
     */
    public Long hsetObject(String key, String field, Object value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 获取指定key，field的值value
     *
     * @param key
     * @param field
     */
    public String hget(String key, String field) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 获取指定key，field的值value
     *
     * @param key
     * @param field
     */
    public byte[] hget(byte[] key, byte[] field) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 获取指定key，field的值value
     *
     * @param key
     * @param field
     */
    public Object hgetObject(String key, String field) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 将哈希表 key 中的域 field 的值设置为 value
     *
     * @param key
     * @param field
     * @param value
     */
    public Long hsetnx(String key, String field, String value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 将哈希表 key 中的域 field 的值设置为 value
     *
     * @param key
     * @param field
     * @param value
     */
    public Long hsetnx(byte[] key, byte[] field, byte[] value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 将哈希表 key 中的域 field 的值设置为 value
     *
     * @param key
     * @param field
     * @param value
     */
    public Long hsetnxObject(String key, String field, Object value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回哈希表 key中field的数量。
     *
     * @param key
     */
    public Long hlen(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回哈希表 key中field的数量。
     *
     * @param key
     */
    public Long hlen(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 查看哈希表 key 中，给定域 field 是否存在
     *
     * @param key
     * @param field
     */
    public Boolean hexists(String key, String field) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 查看哈希表 key 中，给定域 field 是否存在
     *
     * @param key
     * @param field
     */
    public Boolean hexists(byte[] key, byte[] field) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 获取所有字段名保存在键的哈希值
     *
     * @param key
     */
    public Set<String> hkeys(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 获取所有字段名保存在键的哈希值
     *
     * @param key
     */
    public Set<byte[]> hkeys(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回哈希表 key 中的所有field的值
     *
     * @param key
     */
    public List<String> hvals(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回哈希表 key 中的所有field的值
     *
     * @param key
     */
    public Collection<byte[]> hvals(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }


    public Long hdel(String key, String... field) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long hdel(byte[] key, byte[]... field){
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存集合的 member 元素将被忽略。
     *
     * @param key
     */
    public Long sadd(String key, String... member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存集合的 member 元素将被忽略。
     *
     * @param key
     */
    public Long sadd(byte[] key, byte[]... member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回集合 key 的集合中元素的数量。
     *
     * @param key
     */
    public Long scard(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回集合 key 的集合中元素的数量。
     *
     * @param key
     */
    public Long scard(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }


    /**
     * 返回集合 key 中的所有成员。
     *
     * @param key
     */
    public Set<String> smembers(String key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 返回集合 key 中的所有成员。
     *
     * @param key
     */
    public Set<byte[]> smembers(byte[] key) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 操作有序的Set
     *
     * @param key
     * @param score
     * @param member
     */
    public Long zadd(String key, double score, String member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 操作有序的Set
     *
     * @param key
     * @param score
     * @param member
     */
    public Long zadd(byte[] key, double score, byte[] member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Double zincrby(byte[] key, double score, byte[] member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }


    public Double zincrby(String key, double score, String member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 取分数在min和max之间的值，返回Set<String member>
     *
     * @param key
     */
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 取分数在min和max之间的值，返回Set<String member>
     *
     * @param key
     */
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 取分数在min和max之间的值，返回Set<String member>
     *
     * @param key
     */
    public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min, int offset, int count) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long zremrangeByScore(String key, double min, double max) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long zremrangeByScore(String key, String min, String max) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long zremrangeByScore(byte[] key, double min, double max) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long zremrangeByScore(byte[] key, byte[] min, byte[] max) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 从zset中删除指定key并且value=member的值，可以一次删除多行
     *
     * @param key
     */
    public Long zrem(String key, String... member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    /**
     * 从zset中删除指定key并且value=member的值，可以一次删除多行
     *
     * @param key
     */
    public Long zrem(byte[] key, byte[]... member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<String> keys(String pattern) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }
    public String rpop(String key){
    	throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    };

    public byte[] rpop(byte[] key){
    	throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    };

	public boolean ltrim(String key, long start, long end) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public boolean ltrim(byte[] key, long start, long end) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public Long rpush(byte[] key, byte[]... values) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

    @Deprecated
	public String rpoplpush(String srckey, String dstkey) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

    @Deprecated
	public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public Long linsert(String key, LIST_POSITION where, String pivot, String value)  {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public Long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value)  {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public Long lpushx(String key, String... values) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public Long lpushx(byte[] key, byte[]... values) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public Long rpushx(String key, String... values) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public Long rpushx(byte[] key, byte[]... values) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

    public Double zscore(String key, String member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Double zscore(byte[] key, byte[] member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<String> zrange(String key, long start, long end) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<byte[]> zrange(byte[] key, long start, long end) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<Tuple> zrangeWithScores(byte[] key, long start, long end) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max, int offset, int count) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<String> zrevrange(String key, long start, long end) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Set<byte[]> zrevrange(byte[] key, long start, long end) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long zrank(String key, String member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long zrank(byte[] key, byte[] member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long zrevrank(String key, String member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long zrevrank(byte[] key, byte[] member) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public List<String> mget(String... keys) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public List<byte[]> mget(byte[]... keys) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    @Override
    public Long publish(String channel, String message) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    @Override
    public Long publish(byte[] channel, byte[] message) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    @Override
    public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    @Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    @Override
    public void psubscribe(BinaryJedisPubSub jedisPubSub, byte[]... patterns) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

	public String spop(String key) {
		 throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public byte[] spop(byte[] key) {
		 throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public Long srem(String key, String... member) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}

	public Long srem(byte[] key, byte[]... member) {
		throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
	}


    public Long setnx(String key, String value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }

    public Long setnx(byte[] key, byte[] value) {
        throw new CacheException(EXECPTION_MESSAGE.AbsentImplementMethod);
    }
}




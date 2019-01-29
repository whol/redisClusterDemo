package com.example.demo.cache.service;

import com.example.demo.cache.log.MultiKeys;
import com.example.demo.cache.log.NoKey;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Tuple;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICacheService {

    /**
     * 判断某个Key是否存在
     *
     * @param key
     */
    boolean isExist(String key);


    /**
     * 判断某个Key是否存在
     *
     * @param key
     */
    boolean isExist(byte[] key);

    /**
     * 根据key从缓存中删除
     *
     * @param key
     */
    Long del(String key);

    /**
     * 根据key从缓存中删除
     *
     * @param key
     */
    Long del(byte[] key);

    /**
     * 从缓存中删除一个或多个key，不存在的 key 会被忽略。
     *
     * @param keys
     */
    @MultiKeys
    Long del(byte[]... keys);

    /**
     * 从缓存中删除一个或多个key，不存在的 key 会被忽略。
     *
     * @param keys
     */
    @MultiKeys
    Long del(String... keys);

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间
     *
     * @param key
     */
    Long ttl(String key);

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间
     *
     * @param key
     */
    Long ttl(byte[] key);

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     *
     * @param key
     */
    Long expire(String key, int seconds);

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     *
     * @param key
     */
    Long expire(byte[] key, int seconds);

    /**
     * 放入缓存,永久生效
     *
     * @param key
     * @param value
     */
    boolean setString(String key, String value);

    /**
     * 放入缓存,永久生效
     *
     * @param key
     * @param value
     */
    @Deprecated
    boolean setByte(String key, byte[] value);

    /**
     * 放入缓存,永久生效
     *
     * @param key
     * @param value
     */
    boolean setByte(byte[] key, byte[] value);

    /**
     * 放入缓存,永久生效
     *
     * @param key
     * @param value
     */
    boolean setObject(String key, Object value);

    /**
     * 放入缓存,设置生效时间
     *
     * @param key
     * @param value
     * @param seconds
     */
    boolean setex(String key, String value, int seconds);

    /**
     * 放入缓存,设置生效时间
     *
     * @param key
     * @param value
     * @param seconds
     */
    boolean setex(byte[] key, byte[] value, int seconds);

    /**
     * 放入缓存,设置生效时间
     *
     * @param key
     * @param value
     * @param seconds
     */
    boolean setexObject(String key, Object value, int seconds);

    /**
     * 从缓存中获取信息
     *
     * @param key
     */
    String getString(String key);

    /**
     * 从缓存中获取信息
     *
     * @param key
     */
    @Deprecated
    byte[] getByte(String key);

    /**
     * 从缓存中获取信息
     *
     * @param key
     */
    byte[] getByte(byte[] key);

    /**
     * 从缓存中获取信息
     *
     * @param key
     */
    Object getObject(String key);


    /**
     * 递增序列，传参为String类型
     *
     * @param key
     */
    Long incr(String key);

    /**
     * 将 key 所储存的值加上给定的增量值（increment） ,传参key为String类型
     *
     * @param key
     * @param integer
     */
    Long incrBy(String key, long integer);

    /**
     * 递增序列，传参为byte类型
     *
     * @param key
     */
    Long incr(byte[] key);

    /**
     * 将 key 所储存的值加上给定的增量值（increment） ,传参key为byte类型
     *
     * @param key
     * @param integer
     */
    Long incrBy(byte[] key, long integer);

    /**
     * 递减序列，传参为String类型
     *
     * @param key
     */
    Long decr(String key);

    /**
     * key 所储存的值减去给定的减量值（decrement） ,传参key为String类型
     *
     * @param key
     * @param integer
     */
    Long decrBy(String key, long integer);

    /**
     * 递减序列，传参为byte类型
     *
     * @param key
     */
    Long decr(byte[] key);

    /**
     * key 所储存的值减去给定的减量值（decrement） ,传参key为byte类型
     *
     * @param key
     * @param integer
     */
    Long decrBy(byte[] key, long integer);
    /**
     * 返回列表 key的长度
     *
     * @param key
     */
    Long llen(byte[] key);

    /**
     * 返回列表 key的长度
     *
     * @param key
     */
    Long llen(String key);

    /**
     * 将值 value 插入到列表 key 的表头
     *
     * @param key
     * @param values
     */
    Long lpush(byte[] key, byte[]... values);

    /**
     * 将值 value 插入到列表 key 的表头
     *
     * @param key
     * @param values
     */
    Long lpush(String key, String... values);

    /**
     * 将值 value 插入到列表 key 的表尾
     *
     * @param key
     * @param values
     */
    Long rpush(String key, String... values);

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定
     *
     * @param key
     * @param start
     * @param end
     */
    List<String> lrange(String key, long start, long end);

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定
     *
     * @param key
     * @param start
     * @param end
     */
    List<byte[]> lrange(byte[] key, long start, long end);

    /**
     * 返回列表 key 中，下标为 index 的元素。
     *
     * @param key
     * @param index
     */
    byte[] lindex(byte[] key, long index);

    /**
     * 返回列表 key 中，下标为 index 的元素。
     *
     * @param key
     * @param index
     */
    String lindex(String key, long index);

    /**
     * 移除并返回列表 key 的头元素。
     *
     * @param key
     */
    byte[] lpop(byte[] key);

    /**
     * 移除并返回列表 key 的头元素。
     *
     * @param key
     */
    String lpop(String key);

    /***
     * 根据参数 count 的值，移除列表中与参数 value 相等的元素
     * @param key 列表的key
     * @param count <p>count 的值可以是以下几种：
     * 	count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count
     * 	count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值
     * 	count = 0 : 移除表中所有与 value 相等的值</p>
     * @param value 值
     * @return 被移除元素的数量, 因为不存在的 key 被视作空表(empty list)，所以当 key 不存在时，返回 0
     */
    Long lrem(String key, long count, String value);

    /***
     * 根据参数 count 的值，移除列表中与参数 value 相等的元素
     * @param key 列表的key
     * @param count <p>count 的值可以是以下几种：
     * 	count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count
     * 	count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值
     * 	count = 0 : 移除表中所有与 value 相等的值</p>
     * @param value 值
     * @return 被移除元素的数量, 因为不存在的 key 被视作空表(empty list)，所以当 key 不存在时，返回 0
     */
    Long lrem(byte[] key, long count, byte[] value);

    /**
     * 将Map放入缓存，ok成功
     *
     * @param key
     */
    String putMap(String key, Map<String, String> map);

    /**
     * 将Map放入缓存，ok成功
     *
     * @param key
     */
    String putMap(byte[] key, Map<byte[], byte[]> map);

    /**
     * 从缓存中读取map，如果存在key值，则返回Map对象，如果不存在，则返回null
     *
     * @param key
     */
    Map<String, String> getMap(String key);

    /**
     * 从缓存中读取map，如果存在key值，则返回Map对象，如果不存在，则返回null
     *
     * @param key
     */
    Map<byte[], byte[]> getMap(byte[] key);

    /**
     * 设置指定key，field的值value
     *
     * @param key
     * @param field
     * @param value
     */
    Long hset(String key, String field, String value);

    /**
     * 设置指定key，field的值value
     *
     * @param key
     * @param field
     * @param value
     */
    Long hset(byte[] key, byte[] field, byte[] value);

    /**
     * 设置指定key，field的值value
     *
     * @param key
     * @param field
     * @param value
     */
    Long hsetObject(String key, String field, Object value);

    /**
     * 获取指定key，field的值value
     *
     * @param key
     * @param field
     */
    String hget(String key, String field);

    /**
     * 获取指定key，field的值value
     *
     * @param key
     * @param field
     */
    byte[] hget(byte[] key, byte[] field);

    /**
     * 获取指定key，field的值value
     *
     * @param key
     * @param field
     */
    Object hgetObject(String key, String field);

    /**
     * 将哈希表 key 中的域 field 的值设置为 value
     *
     * @param key
     * @param field
     * @param value
     */
    Long hsetnx(String key, String field, String value);

    /**
     * 将哈希表 key 中的域 field 的值设置为 value
     *
     * @param key
     * @param field
     * @param value
     */
    Long hsetnxObject(String key, String field, Object value);

    /**
     * 将哈希表 key 中的域 field 的值设置为 value
     *
     * @param key
     * @param field
     * @param value
     */
    Long hsetnx(byte[] key, byte[] field, byte[] value);

    /**
     * 返回哈希表 key中field的数量。
     *
     * @param key
     */
    Long hlen(String key);

    /**
     * 返回哈希表 key中field的数量。
     *
     * @param key
     */
    Long hlen(byte[] key);

    /**
     * 查看哈希表 key 中，给定域 field 是否存在
     *
     * @param key
     * @param field
     */
    Boolean hexists(String key, String field);

    /**
     * 查看哈希表 key 中，给定域 field 是否存在
     *
     * @param key
     * @param field
     */
    Boolean hexists(byte[] key, byte[] field);

    /**
     * 获取所有字段名保存在键的哈希值
     *
     * @param key
     */
    Set<String> hkeys(String key);

    /**
     * 获取所有字段名保存在键的哈希值
     *
     * @param key
     */
    Set<byte[]> hkeys(byte[] key);

    /**
     * 返回哈希表 key 中的所有field的值
     *
     * @param key
     */
    List<String> hvals(String key);

    /**
     * 返回哈希表 key 中的所有field的值
     *
     * @param key
     */
    Collection<byte[]> hvals(byte[] key);

    /**
     * 从哈希表删除field
     *
     * @param key   哈希表的key
     * @param field field名称
     */
    Long hdel(String key, String... field);

    /**
     * 从哈希表删除field
     *
     * @param key   哈希表的key
     * @param field field名称
     */
    Long hdel(byte[] key, byte[]... field);

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存集合的 member 元素将被忽略。
     *
     * @param key
     */
    Long sadd(String key, String... member);

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存集合的 member 元素将被忽略。
     *
     * @param key
     */
    Long sadd(byte[] key, byte[]... member);

    /**
     * 返回集合 key 的集合中元素的数量。
     *
     * @param key
     */
    Long scard(String key);

    /**
     * 返回集合 key 的集合中元素的数量。
     *
     * @param key
     */
    Long scard(byte[] key);


    /**
     * 返回集合 key 中的所有成员。
     *
     * @param key
     */
    Set<String> smembers(String key);

    /**
     * 返回集合 key 中的所有成员。
     *
     * @param key
     */
    Set<byte[]> smembers(byte[] key);

    /**
     * 操作有序的Set
     *
     * @param key
     * @param score
     * @param member
     */
    Long zadd(String key, double score, String member);

    /**
     * 操作有序的Set
     *
     * @param key
     * @param score
     * @param member
     */
    Long zadd(byte[] key, double score, byte[] member);

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 increment
     *
     * @param key 有序集的key
     * @param score score 值增量
     * @param member 有序集中的某个成员 member
     */
    Double zincrby(byte[] key, double score, byte[] member);

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 increment
     *
     * @param key 有序集的key
     * @param score score 值增量
     * @param member 有序集中的某个成员 member
     */
    Double zincrby(String key, double score, String member);

    /**
     * 取分数在min和max之间的值，返回Set<String member>
     *
     * @param key
     * @param max
     * @param min
     */
    Set<String> zrevrangeByScore(String key, double max, double min);

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员, 支持分页
     *
     * @param key 有序集的key
     * @param max score值
     * @param min score值
     * @param offset 开始位置
     * @param count 获取数量
     * @return 所有的成员
     */
    Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count);

    /**
     * 取分数在min和max之间的值，返回Set<byte[] member>
     *
     * @param key
     * @param max
     * @param min
     */
    Set<byte[]> zrevrangeByScore(byte[] key, double max, double min);

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员, 支持分页
     * <p>其中成员的位置按 score 值递增(从大到小)来排序</p>
     * @param key 有序集的key
     * @param max score值
     * @param min score值
     * @param offset 开始位置
     * @param count 获取数量
     */
    Set<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count);

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员, 带 score 值
     * <p>其中成员的位置按 score 值递增(从大到小)来排序</p>
     * @param key 有序集的key
     * @param max score值
     * @param min score值
     */
    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min);

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员, 带 score 值
     * <p>其中成员的位置按 score 值递增(从大到小)来排序</p>
     * @param key 有序集的key
     * @param max score值
     * @param min score值
     */
    Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min);

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员, 带 score 值, 支持分页
     * <p>其中成员的位置按 score 值递增(从大到小)来排序</p>
     * @param key 有序集的key
     * @param max score值
     * @param min score值
     * @param offset 开始位置
     * @param count 获取数量
     */
    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count);

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员, 带 score 值, 支持分页
     * <p>其中成员的位置按 score 值递增(从大到小)来排序</p>
     * @param key 有序集的key
     * @param max score值
     * @param min score值
     * @param offset 开始位置
     * @param count 获取数量
     */
    Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min, int offset, int count);

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 权重区间之间的成员
     *
     * @param key 序集 key
     * @param min score 下限权重(包含)
     * @param max score 上限权重(包含)
     * @return 被移除成员的数
     */
    Long zremrangeByScore(String key, double min, double max);

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 权重区间之间的成员
     * <p>自Redis版本2.1.6开始， score 值等于 min 或 max 的成员也可以不包括在内, 例如: min ="(1", max="(100"
     *
     * @param key 序集 key
     * @param min score 下限权重
     * @param max score 上限权重
     * @return 被移除成员的数
     */
    Long zremrangeByScore(String key, String min, String max);

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 权重区间之间的成员
     *
     * @param key 序集 key
     * @param min score 下限权重(包含)
     * @param max score 上限权重(包含)
     * @return 被移除成员的数
     */
    Long zremrangeByScore(byte[] key, double min, double max);

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 权重区间之间的成员
     * <p>自Redis版本2.1.6开始， score 值等于 min 或 max 的成员也可以不包括在内, 例如: min ="(1", max="(100"
     *
     * @param key 序集 key
     * @param min score 下限权重
     * @param max score 上限权重
     * @return 被移除成员的数
     */
    Long zremrangeByScore(byte[] key, byte[] min, byte[] max);

    /**
     * 从zset中删除指定key并且value=member的值，可以一次删除多行
     *
     * @param key
     * @param member
     */
    Long zrem(String key, String... member);

    /**
     * 从zset中删除指定key并且value=member的值，可以一次删除多行
     *
     * @param key
     * @param member
     */
    Long zrem(byte[] key, byte[]... member);

    /**
     * 模糊查询功能，通过表达式进行匹配
     * <p><span style="color:red;">警告: 该接口被设计为用于调试,尽量避免在生产环境使用!!!</span></p>
     * <p>查找所有符合给定模式 pattern 的 key
     * <p>KEYS * 匹配缓存中所有 key
     * <p>KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * <p>KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * <p>KEYS h[ae]llo 匹配 hello 和 hallo, 但不匹配 hillo
     * <p>特殊符号用 \ 隔开
     *
     * @param pattern 表达式
     */
    Set<String> keys(String pattern);

    /**
     * 移除并返回列表 key 的尾元素。
     *
     * @param key
     *
     */
    String rpop(String key);

    /**
     * 移除并返回列表 key 的尾元素。
     *
     * @param key
     *
     */
    byte[] rpop(byte[] key);

    /**
     * 列表修剪(trim)，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     *<p>可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     *<p>当 key不是列表类型时，返回一个错误。
     * @param key
     *
     */
	boolean ltrim(String key, long start, long end);
    /**
     * 列表修剪(trim)，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * <p>可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * <p>当 key不是列表类型时，返回一个错误。
     * @param key
     *
     */
	boolean ltrim(byte[] key, long start, long end) ;

    /**
     * 将值 value 插入到列表 key 的表尾
     *
     * @param key
     * @param values
     */
	Long rpush(byte[] key, byte[]... values);

    /**
     * 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端,将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
     * <p><span style="color:red;">警告: 命令中的key必须属于一个slot，否则出现异常</span></p>
     * <p>如果 source 不存在，值 nil 被返回，并且不执行其他动作。
     * <p>如果 source 和 destination 相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。
     * @param srckey
     * @param dstkey
     */
    @Deprecated
	String rpoplpush(String srckey, String dstkey);

    /**
     * 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端,将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
     * <p><span style="color:red;">警告: 命令中的key必须属于一个slot，否则出现异常</span></p>
     * <p>如果 source 不存在，值 nil 被返回，并且不执行其他动作。
     * <p>如果 source 和 destination 相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。
     * @param srckey
     * @param dstkey
     */
    @Deprecated
	byte[] rpoplpush(byte[] srckey, byte[] dstkey);

    /**
     * 将值 value 插入到列表 key当中，位于值 pivot之前或之后。
     * <p>当 pivot 不存在于列表 key 时，不执行任何操作。
     * <p>当 key 不存在时， key 被视为空列表，不执行任何操作。
     * <p>如果 key 不是列表类型，返回一个错误。
     * @param key
     * @param where
     * @param pivot
     * @param value
     */
	Long linsert(String key, LIST_POSITION where, String pivot, String value);
    /**
     * 将值 value 插入到列表 key当中，位于值 pivot之前或之后。
     * <p>当 pivot 不存在于列表 key 时，不执行任何操作。
     * <p>当 key 不存在时， key 被视为空列表，不执行任何操作。
     * <p>如果 key 不是列表类型，返回一个错误。
     * @param key
     * @param where
     * @param pivot
     * @param value
     */
	Long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value);

    /**
     * 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。
     * <p>和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做。
     * @param key
     * @param values
     */
	Long lpushx(String key, String... values);

    /**
     * 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。
     * <p>和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做。
     * @param key
     * @param values
     */
	Long lpushx(byte[] key, byte[]... values);

    /**
     * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表。
     * <p>和 RPUSH 命令相反，当 key 不存在时， RPUSHX 命令什么也不做。
     * @param key
     * @param values
     */
	Long rpushx(String key, String... values);

    /**
     * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表。
     * <p>和 RPUSH 命令相反，当 key 不存在时， RPUSHX 命令什么也不做。
     * @param key
     * @param values
     */
	Long rpushx(byte[] key, byte[]... values);

    /**
     * 返回有序集key中，成员member的score值
     *
     * @param key 有序集key
     * @param member 成员member
     * @return score值
     */
    Double zscore(String key, String member);

    /**
     * 返回有序集key中，成员member的score值
     *
     * @param key 有序集key
     * @param member 成员member
     * @return score值
     */
    Double zscore(byte[] key, byte[] member);

    /**
     * 返回有序集 key 中，指定区间内的成员
     * <p>其中成员的位置按 score 值递增(从小到大)来排序</p>
     * <p>下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
     你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推</p>
     *
     * @param key 有序集key
     * @param start 下标参数
     * @param end 下标参数
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表
     */
    Set<String> zrange(final String key, final long start, final long end);

    /**
     * 返回有序集 key 中，指定区间内的成员
     * <p>其中成员的位置按 score 值递增(从小到大)来排序</p>
     * <p>下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
     你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推</p>
     *
     * @param key 有序集key
     * @param start 下标参数
     * @param end 下标参数
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表
     */
    Set<byte[]> zrange(byte[] key, long start, long end);

    /**
     * 返回有序集 key 中，指定区间内的成员(带score值)
     * <p>其中成员的位置按 score 值递增(从小到大)来排序</p>
     * <p>下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
     你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推</p>
     *
     * @param key 有序集key
     * @param start 下标参数
     * @param end 下标参数
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表
     */
    Set<Tuple> zrangeWithScores(String key, long start, long end);

    /**
     * 返回有序集 key 中，指定区间内的成员(带score值)
     * <p>其中成员的位置按 score 值递增(从小到大)来排序</p>
     * <p>下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
     你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推</p>
     *
     * @param key 有序集key
     * @param start 下标参数
     * @param end 下标参数
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表
     */
    Set<Tuple> zrangeWithScores(byte[] key, long start, long end);

    /**
     * 返回有序集 key 中， score 值介于 min 和 max 之间(默认包括等于 max 或 min )的所有的成员, 带 score 值, 支持分页
     *  <p>其中成员的位置按 score 值递增(从小到大)来排序</p>
     * @param key 有序集的key
     * @param min score值
     * @param max score值
     */
    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max);

    /**
     * 返回有序集 key 中， score 值介于 min 和 max 之间(默认包括等于 max 或 min )的所有的成员, 带 score 值, 支持分页
     * <p>其中成员的位置按 score 值递增(从小到大)来排序</p>
     * @param key 有序集的key
     * @param min score值
     * @param max score值
     * @param offset 开始位置
     * @param count 获取数量
     */
    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count);

    /**
     * 返回有序集 key 中， score 值介于 min 和 max 之间(默认包括等于 max 或 min )的所有的成员, 带 score 值, 支持分页
     *  <p>其中成员的位置按 score 值递增(从小到大)来排序</p>
     * @param key 有序集的key
     * @param min score值
     * @param max score值
     */
    Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max);

    /**
     * 返回有序集 key 中， score 值介于 min 和 max 之间(默认包括等于 max 或 min )的所有的成员, 带 score 值, 支持分页
     * <p>其中成员的位置按 score 值递增(从小到大)来排序</p>
     * @param key 有序集的key
     * @param min score值
     * @param max score值
     * @param offset 开始位置
     * @param count 获取数量
     */
    Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max, int offset, int count);

    /**
     * 返回有序集 key 中，指定区间内的成员
     * <p>其中成员的位置按 score 值递增(从大到小)来排序</p>
     * <p>下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
     你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推</p>
     *
     * @param key 有序集key
     * @param start 下标参数
     * @param end 下标参数
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表
     */
    Set<String> zrevrange(final String key, final long start, final long end);

    /**
     * 返回有序集 key 中，指定区间内的成员
     * <p>其中成员的位置按 score 值递增(从大到小)来排序</p>
     * <p>下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
     你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推</p>
     *
     * @param key 有序集key
     * @param start 下标参数
     * @param end 下标参数
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表
     */
    Set<byte[]> zrevrange(byte[] key, long start, long end);

    /**
     * 返回有序集key中成员member的排名
     * <p>其中有序集成员按score值递增(从小到大)顺序排列</p>
     *
     * @param key 有序集key
     * @param member 成员member
     * @return 排名
     */
    Long zrank(final String key, final String member);

    /**
     * 返回有序集key中成员member的排名
     * <p>其中有序集成员按score值递增(从小到大)顺序排列</p>
     *
     * @param key 有序集key
     * @param member 成员member
     * @return 排名
     */
    Long zrank(byte[] key, byte[] member);

    /**
     * 返回有序集key中成员member的排名
     * <p>其中有序集成员按score值从大到小排列</p>
     *
     * @param key 有序集key
     * @param member 成员member
     * @return 排名
     */
    Long zrevrank(final String key, final String member);

    /**
     * 返回有序集key中成员member的排名
     * <p>其中有序集成员按score值从大到小排列</p>
     *
     * @param key 有序集key
     * @param member 成员member
     * @return 排名
     */
    Long zrevrank(byte[] key, byte[] member);

    /**
     * 获取所有指定键的值
     *
     * @param keys 指定键
     * @return 值的列表
     */
    @MultiKeys
    List<String> mget(final String... keys);

    /**
     * 获取所有指定键的值
     *
     * @param keys 指定键
     * @return 值的列表
     */
    @MultiKeys
    List<byte[]> mget(byte[]... keys);

    /**
     * 将信息 message 发送到指定的频道 channel
     *
     * @param channel 频道
     * @param message 信息
     * @return 接收到信息 message 的订阅者数量
     */
    @NoKey
    Long publish(String channel, String message);

    /**
     * 将信息 message 发送到指定的频道 channel
     *
     * @param channel 频道
     * @param message 信息
     * @return 接收到信息 message 的订阅者数量
     */
    @NoKey
    Long publish(byte[] channel, byte[] message);

    /**
     * 订阅给定频道的信息
     *
     * @param jedisPubSub 订阅回调(需要自己实现一个类,继承JedisPubSub类,复写其onMessage方法获取消息)
     * @param channels 频道
     */
    @NoKey
    void subscribe(JedisPubSub jedisPubSub, final String... channels);

    /**
     * 订阅给定频道的信息
     *
     * @param jedisPubSub 订阅回调(需要自己实现一个类,继承BinaryJedisPubSub类,复写其onMessage方法获取消息)
     * @param channels 频道
     */
    @NoKey
    void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels);

    /**
     * 订阅符合给定模式的频道
     *
     * @param jedisPubSub 订阅回调(需要自己实现一个类,继承JedisPubSubb类,复写其onPMessage方法获取消息)
     * @param patterns 模式, 每个模式以 * 作为匹配符，比如 huangz* 匹配所有以 huangz 开头的频道( huangzmsg 、 huangz-blog 、 huangz.tweets 等等)， news.* 匹配所有以 news. 开头的频道( news.it 、 news.global.today 等等)，诸如此类
     */
    void psubscribe(JedisPubSub jedisPubSub, String... patterns);

    /**
     * 订阅符合给定模式的频道
     *
     * @param jedisPubSub 订阅回调(需要自己实现一个类,继承BinaryJedisPubSub类,复写其onPMessage方法获取消息)
     * @param patterns 模式, 每个模式以 * 作为匹配符，比如 huangz* 匹配所有以 huangz 开头的频道( huangzmsg 、 huangz-blog 、 huangz.tweets 等等)， news.* 匹配所有以 news. 开头的频道( news.it 、 news.global.today 等等)，诸如此类
     */
    @NoKey
    void psubscribe(BinaryJedisPubSub jedisPubSub, byte[]... patterns);

    /**
     * 移除并返回集合中的一个随机元素
     *
     * @param key
     * <p>当 key 不存在或 key 是空时，返回 null 。
     */
	String spop(String key);

    /**
     * 移除并返回集合中的一个随机元素
     *
     * @param key
     * <p>当 key 不存在或 key 是空时，返回 null 。
     */
    byte[] spop(byte[] key);

    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略
     *
     * @param key
     * @param member
     * <p>当 key 不是集合类型，返回一个错误。
     */
	Long srem(String key, String... member);

    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略
     *
     * @param key
     * @param member
     * <p>当 key 不是集合类型，返回一个错误。
     */
	Long srem(byte[] key, byte[]... member);

    /**
     * 将key设置值为value，如果key不存在，这种情况下等同SET命令。 当key存在时，什么也不做
     * @param key
     * @param value
     * @return 设置成功返回1，失败返回0
     */
    Long setnx(String key, String value);

    /**
     * 将key设置值为value，如果key不存在，这种情况下等同SET命令。 当key存在时，什么也不做
     * @param key
     * @param value
     * @return 设置成功返回1，失败返回0
     */
    Long setnx(byte[] key, byte[] value);
}

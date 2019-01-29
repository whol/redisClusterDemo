package com.example.demo.cache.redis;

import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 订阅注册
 *
 * @author zhuanghd
 * @since 1.0
 */
public class SubRegistration {

    // 注册中心
    private static final Map<String, Map<String, Map>> REGISTRATION = new HashMap();

    private static final Map<String, JedisPubSub> CHANNEL_SUB = new ConcurrentHashMap();
    private static final Map<byte[], BinaryJedisPubSub> BIN_CHANNEL_SUB = new ConcurrentHashMap();
    private static final Map<String, JedisPubSub> PATTERN_SUB = new ConcurrentHashMap();
    private static final Map<byte[], BinaryJedisPubSub> BIN_PATTERN_SUB = new ConcurrentHashMap();

    // 单集群
    private static final String SINGLE_CLUSTER = "SINGLE_CLUSTER";

    private static final String CHANNEL_SUB_REG = "CHANNEL_SUB";
    private static final String BIN_CHANNEL_SUB_REG = "BIN_CHANNEL_SUB";
    private static final String PATTERN_SUB_REG = "PATTERN_SUB";
    private static final String BIN_PATTERN_SUB_REG = "BIN_PATTERN_SUB";

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    static {
        Set<String> clusters = JedisClusterFactory.getAllClusterNames();
        if(clusters != null && clusters.size() > 0) {
            for (String cluster : clusters) {
                REGISTRATION.put(cluster, createRegisterMap());
            }
        } else {
            REGISTRATION.put(SINGLE_CLUSTER, createRegisterMap());
        }
    }

    /**
     * 注册订阅
     * @param channel 频道
     * @param jedisPubSub 订阅回调
     */
    public static void registerChannelSub(String clusterName, JedisPubSub jedisPubSub, String... channel) {
        for(String c : channel) {
            Map map = (Map) getRegisterMap(clusterName).get(CHANNEL_SUB_REG);
            map.put(c, jedisPubSub);
        }
    }

    /**
     * 注册二进制订阅
     * @param channel 频道
     * @param jedisPubSub 订阅回调
     */
    public static void registerBinaryChannelSub(String clusterName, BinaryJedisPubSub jedisPubSub, byte[]... channel) {
        for(byte[] c : channel) {
            Map map = (Map) getRegisterMap(clusterName).get(BIN_CHANNEL_SUB_REG);
            map.put(c, jedisPubSub);
        }
    }

    /**
     * 注册模式订阅
     * @param pattern 模式
     * @param jedisPubSub 订阅回调
     */
    public static void registerPatternSub(String clusterName, JedisPubSub jedisPubSub, String... pattern) {
        for(String p : pattern) {
            Map map = (Map) getRegisterMap(clusterName).get(PATTERN_SUB_REG);
            map.put(p, jedisPubSub);
        }
    }

    /**
     * 注册模式二进制订阅
     * @param pattern 模式
     * @param jedisPubSub 订阅回调
     */
    public static void registerBinaryPatternSub(String clusterName, BinaryJedisPubSub jedisPubSub, byte[]... pattern) {
        for(byte[] p : pattern) {
            Map map = (Map) getRegisterMap(clusterName).get(BIN_PATTERN_SUB_REG);
            map.put(p, jedisPubSub);
        }
    }

    /**
     * 重连订阅器
     *
     * @param clusterName 集群名称
     * @param jedisCluster 集群对象
     */
    public static void reconnectSubscribers(final String clusterName, final JedisCluster jedisCluster) {
        Map reg = getRegisterMap(clusterName);
        if(reg == null) {
            return;
        }
        threadPool.shutdown();
        threadPool = Executors.newCachedThreadPool();
        // 重连订阅
        final Map channelSubs = (Map) reg.get(CHANNEL_SUB_REG);
        Set<String> c1Set = new HashSet(channelSubs.keySet());
        for(final String channel : c1Set) {
            threadPool.submit(new Runnable() {
                public void run() {
                    JedisPubSub sub = (JedisPubSub) channelSubs.get(channel);
                    if(sub.isSubscribed()) {
                        JedisClusterFactory.getJedisCluster(clusterName).subscribe(sub, channel);
                    } else {
                        channelSubs.remove(channel);
                    }
                }
            });
        }
        // 重连二进制订阅
        final Map binChannelSubs = (Map) reg.get(BIN_CHANNEL_SUB_REG);
        Set<byte[]> c2Set = new HashSet(binChannelSubs.keySet());
        for(final byte[] channel : c2Set) {
            threadPool.submit(new Runnable() {
                public void run() {
                    BinaryJedisPubSub sub = (BinaryJedisPubSub) binChannelSubs.get(channel);
                    if(sub.isSubscribed()) {
                        JedisClusterFactory.getJedisCluster(clusterName).subscribe(sub, channel);
                    } else {
                        binChannelSubs.remove(channel);
                    }
                }
            });
        }
        // 重连模式订阅
        final Map patternSubs = (Map) reg.get(PATTERN_SUB_REG);
        Set<String> c3Set = new HashSet(patternSubs.keySet());
        for(final String pattern : c3Set) {
            threadPool.submit(new Runnable() {
                public void run() {
                    JedisPubSub sub = (JedisPubSub) patternSubs.get(pattern);
                    if(sub.isSubscribed()) {
                        JedisClusterFactory.getJedisCluster(clusterName).psubscribe(sub, pattern);
                    } else {
                        patternSubs.remove(pattern);
                    }
                }
            });
        }
        // 重连模式二进制订阅
        final Map binPatternSubs = (Map) reg.get(BIN_PATTERN_SUB_REG);
        Set<byte[]> c4Set = new HashSet(binPatternSubs.keySet());
        for(final byte[] pattern : c4Set) {
            threadPool.submit(new Runnable() {
                public void run() {
                    BinaryJedisPubSub sub = (BinaryJedisPubSub) binPatternSubs.get(pattern);
                    if(sub.isSubscribed()) {
                        JedisClusterFactory.getJedisCluster(clusterName).psubscribe(sub, pattern);
                    } else {
                        binPatternSubs.remove(pattern);
                    }
                }
            });
        }
    }

    /**
     * 创建一个Map用于订阅注册
     * @return
     */
    private static Map createRegisterMap() {
        Map map = new HashMap();
        map.put(CHANNEL_SUB_REG, CHANNEL_SUB);
        map.put(BIN_CHANNEL_SUB_REG, BIN_CHANNEL_SUB);
        map.put(PATTERN_SUB_REG, PATTERN_SUB);
        map.put(BIN_PATTERN_SUB_REG, BIN_PATTERN_SUB);
        return map;
    }

    /**
     * 获取订阅注册的Map
     *
     * @param clusterName
     * @return
     */
    private static Map getRegisterMap(String clusterName) {
        String cluster = StringUtils.isBlank(clusterName) ? SINGLE_CLUSTER : clusterName;
        return REGISTRATION.get(cluster);
    }

}

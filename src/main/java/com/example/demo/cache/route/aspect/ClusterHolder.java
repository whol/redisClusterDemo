package com.example.demo.cache.route.aspect;

import redis.clients.jedis.JedisCluster;

/**
 * 简要描述
 * <p>
 * <p> 详细说明
 *
 * @author zhuanghd
 * @see
 * @since 1.0
 */
public class ClusterHolder {

    private static final ThreadLocal<JedisCluster> CLUSTER = new ThreadLocal<JedisCluster>();

    public static void setCluster(JedisCluster cluster) {
        CLUSTER.set(cluster);
    }

    public static JedisCluster getCluster() {
        return CLUSTER.get();
    }

}

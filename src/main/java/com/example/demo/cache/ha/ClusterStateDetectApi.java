package com.example.demo.cache.ha;

import com.example.demo.cache.cacheException.DetectRedisException;
import com.example.demo.cache.config.RouteConfig;
import com.example.demo.cache.log.LogUtil;
import com.example.demo.cache.redis.SubRegistration;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

/**
 * 服务APi缓存集群监控守护线程
 *
 * @author liuyan
 * @since 1.0
 */
@Slf4j
public class ClusterStateDetectApi extends ClusterStateDetectBase {

    private RouteConfig routeConfig = null;

    public void setRouteConfig(RouteConfig routeConfig) {
        this.routeConfig = routeConfig;
    }

    /**
     * cmos-cache服务API缓存高可用状态判断规则：如果整个redis集群不可用则判断redis集群宕机。
     * 实现方法：通过jedisCluster.getClusterNodes()原生jedisAPI获取redis集群的状态信息。
     * 如果集群状态信息cluster_state为fail则集群宕机，如果集群状信息cluster_state为Ok则集群状态可用，
     * 此外如果连接池中没有获取任意redis实例则判断集群宕机。
     */
    public void detect() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        doDetect(routeConfig, log);
                    } catch (Exception e) {
                        LogUtil.writeLog(this.getClass().getName() + ".detect", new DetectRedisException("The thread of ClusterStateApiDetect is error"), "", ClusterStateDetectApi.class);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 设置状态不可用
     * @param clusterName
     */
    @Override
    protected void setNormalFalse(String clusterName) {
        StatFlagForApi.setNormal(clusterName, false);
        LogUtil.writeLog(this.getClass().getName() + "detect", new DetectRedisException("Redis cluster for redisService is down!" + "StatFlagForApi:"
                + StatFlagForApi.isNormal(clusterName)), "", ClusterStateDetectApi.class);
    }

    /**
     * 设置状态可用
     * @param clusterName
     * @param jedisCluster
     */
    @Override
    protected void setNormalTrue(String clusterName, JedisCluster jedisCluster) {
        StatFlagForApi.setNormal(clusterName, true);
        SubRegistration.reconnectSubscribers(clusterName, jedisCluster);
        log.info("Redis cluster for redisService is restoring!" + "StatFlagForApi:"
                + StatFlagForApi.isNormal(clusterName));
    }

    /**
     * 判断节点为可用状态
     * @param clusterInfo
     * @return
     */
    @Override
    protected Boolean judgeOK(String[] clusterInfo) {
        String clusterState = clusterInfo[0].split(":")[1].trim();
        return "ok".equals(clusterState);
    }

    /**
     * 判断节点为不可用状态
     * @param clusterInfo
     * @return
     */
    @Override
    protected Boolean judgeFail(String[] clusterInfo) {
        String clusterState = clusterInfo[0].split(":")[1].trim();
        return "fail".equals(clusterState);
    }

}


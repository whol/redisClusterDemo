package com.example.demo.cache.ha;

import com.example.demo.cache.cacheException.DetectRedisException;
import com.example.demo.cache.config.RouteConfig;
import com.example.demo.cache.log.LogUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

/**
 * Spring cache缓存集群监控守护线程
 *
 * @author liuyan
 * @since 1.0
 */
@Slf4j
public class ClusterStateDetectCache extends ClusterStateDetectBase {


    private RouteConfig routeConfig = null;

    public void setRouteConfig(RouteConfig routeConfig) {
        this.routeConfig = routeConfig;
    }

    /**
     * 缓存切面高可用状态的规则：如果集群中有一对MS挂掉则判断集群状态不可用
     * 实现方法：通过jedisCluster.getClusterNodes()原生jedisAPI获取redis集群的状态信息。
     * 如果集群状态信息为cluster_slots_fail=0则集群可用，如果集群状信息cluster_slots_fail>0为则集群不可用，
     * 此外如果连接池中没有获取任意redis实例判断集群宕机。
     */
    public void detect() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        doDetect(routeConfig, log);
                    } catch (Exception e) {
                        LogUtil.writeLog(this.getClass().getName() + ".detect", new DetectRedisException("The thread of ClusterStateCacheDetectThread is error"), "", ClusterStateDetectApi.class);
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
        LogUtil.writeLog(this.getClass().getName() + "detect", new DetectRedisException("Redis cluster for redisAspect is down!" + "StatFlagForAspect:"
                + StatFlagForAspect.isNormal(clusterName)), "", ClusterStateDetectApi.class);
    }

    /**
     * 设置状态可用
     * @param clusterName
     * @param jedisCluster
     */
    @Override
    protected void setNormalTrue(String clusterName, JedisCluster jedisCluster) {
        StatFlagForAspect.setNormal(clusterName, true);
        log.info("Redis cluster for redisAspect is restoring!" + "StatFlagForAspect:"
                + StatFlagForAspect.isNormal(clusterName));
    }

    /**
     * 判断节点为可用状态
     * @param clusterInfo
     * @return
     */
    @Override
    protected Boolean judgeOK(String[] clusterInfo) {
        String clusterSlotFail = clusterInfo[4].split(":")[1].trim();
        return "0".equals(clusterSlotFail);
    }

    /**
     * 判断节点为不可用状态
     * @param clusterInfo
     * @return
     */
    @Override
    protected Boolean judgeFail(String[] clusterInfo) {
        String clusterSlotFail = clusterInfo[4].split(":")[1].trim();
        return !("0".equals(clusterSlotFail));
    }
}

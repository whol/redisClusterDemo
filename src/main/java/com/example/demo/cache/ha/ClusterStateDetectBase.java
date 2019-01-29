package com.example.demo.cache.ha;

import com.example.demo.cache.config.RouteConfig;
import com.example.demo.cache.redis.JedisClusterFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 缓存集群监控守护线程基类
 * @author gubaofeng
 * @Date 2017/8/17
 * @Time 17:03
 */
@Slf4j
public class ClusterStateDetectBase {
    /**
     * 集群状态不可用分支
     * @param clusterName
     * @param jedisCluster
     * @param logger
     */
    public void isNormalFalse(String clusterName, JedisCluster jedisCluster, Logger logger) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        Map<String, JedisPool> map = new HashMap<String, JedisPool>();
        map = jedisCluster.getClusterNodes();
        if (map == null || map.size() < 1) {
            setNormalFalse(clusterName);
        } else {
            for (Map.Entry<String, JedisPool> entry : map.entrySet()) {
                Jedis resource = null;
                try {
                    resource = entry.getValue().getResource();
                    String[] clusterInfo = resource.clusterInfo().split("\n");
                    if (judgeOK(clusterInfo)) {
                        setNormalTrue(clusterName, jedisCluster);
                        break;
                    }
                } catch (Exception e) {
                } finally {
                    if (resource != null) {
                        resource.close();
                    }
                }
            }
        }
    }

    /**
     * 集群状态可用分支
     * @param clusterName
     * @param jedisCluster
     * @param logger
     */
    public void isNormalTrue(String clusterName, JedisCluster jedisCluster, Logger logger) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        Map<String, JedisPool> map = new HashMap<String, JedisPool>();
        map = jedisCluster.getClusterNodes();
        int failTimes = 0;
        if (map == null || map.size() < 1) {
            setNormalFalse(clusterName);
        } else {
            for (Map.Entry<String, JedisPool> entry : map.entrySet()) {
                Jedis resource = null;
                try {
                    resource = entry.getValue().getResource();
                    String[] clusterInfo = resource.clusterInfo().split("\n");
                    if (judgeFail(clusterInfo)) {
                        setNormalFalse(clusterName);
                        break;
                    }
                } catch (Exception e) {
                    failTimes++;
                } finally {
                    if (resource != null) {
                        resource.close();
                    }
                }
            }
        }
        if (map.size() == failTimes) {
            setNormalFalse(clusterName);
        }
    }

    /**
     * 设置状态不可用
     * @param clusterName
     */
    protected void setNormalFalse(String clusterName) {
    }

    /**
     * 设置状态可用
     * @param clusterName
     * @param jedisCluster
     */
    protected void setNormalTrue(String clusterName, JedisCluster jedisCluster) {
    }

    /**
     * 判断节点为可用状态
     * @param clusterInfo
     * @return
     */
    protected Boolean judgeOK(String[] clusterInfo) {
        return true;
    }

    /**
     * 判断节点为不可用状态
     * @param clusterInfo
     * @return
     */
    protected Boolean judgeFail(String[] clusterInfo) {
        return true;
    }

    /**
     * 线程确认节点状态的调用代码
     * @param routeConfig
     * @param logger
     */
    protected void doDetect(RouteConfig routeConfig, Logger logger) {

        if (routeConfig.usingRoute()) {
            Set<String> clusters = JedisClusterFactory.getAllClusterNames();
            for (String cluster : clusters) {
                performDetect(cluster, JedisClusterFactory.getJedisCluster(cluster), logger);
            }
        } else {
            performDetect(null, JedisClusterFactory.getJedisCluster(), logger);
        }
    }

    /**
     * 执行某个集群的状态检测
     *
     * @param clusterName  集群名称
     * @param jedisCluster 集群客户端
     */
    protected void performDetect(String clusterName, JedisCluster jedisCluster, Logger logger) {
        if (StatFlagForApi.isNormal(clusterName)) {
            isNormalTrue(clusterName, jedisCluster, logger);
        } else {
            isNormalFalse(clusterName, jedisCluster, logger);
        }
    }
}


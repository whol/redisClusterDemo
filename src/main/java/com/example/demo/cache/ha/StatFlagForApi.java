package com.example.demo.cache.ha;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务APi功能开关标识
 *
 * @author liuyan
 * @since 1.0
 */
public class StatFlagForApi {

    // 集群状态集合<集群名称,集群状态>
    private static final Map<String, Boolean> STAT_MAP = new ConcurrentHashMap<>();

    // 单集群模式,集群状态
    private static volatile boolean singleClusterStat = true;

    public static boolean isNormal(String clusterName) {
        return StatFlagUtil.isNormal(clusterName, STAT_MAP, singleClusterStat);
    }

    public static void setNormal(String clusterName, boolean normal) {
        StatFlagUtil.setNormal(clusterName, normal, STAT_MAP, singleClusterStat);
    }

}

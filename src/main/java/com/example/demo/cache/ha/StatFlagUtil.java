package com.example.demo.cache.ha;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author gubaofeng
 * @Date 2017/8/17
 * @Time 19:42
 */
public class StatFlagUtil {
    public static boolean isNormal(String clusterName, Map<String, Boolean> STAT_MAP, boolean singleClusterStat) {
        if (StringUtils.isBlank(clusterName)) {
            return singleClusterStat;
        }
        Boolean stat = STAT_MAP.get(clusterName);
        return stat == null ? true : stat;
    }

    public static void setNormal(String clusterName, boolean normal, Map<String, Boolean> STAT_MAP, boolean singleClusterStat) {
        if (StringUtils.isBlank(clusterName)) {
            singleClusterStat = normal;
        } else {
            STAT_MAP.put(clusterName, normal);
        }
    }
}

package com.example.demo.config;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by Administrator on 2017/8/2.
 */
public class LocalBackupUtils {

    /**
     * 获取本地备份配置文件路径
     * 命名規則
     * {user.dir}/cfgcache/{cmos.system.id}/{cmos.instance.id}.cfgcache
     *
     * @param systemID
     * @param instanceID
     * @return
     */
    public static File getLocalBackupDir(String systemID, String instanceID) {
        // 优先从系统传入参数获取
        String backupDir = System.getProperty("cfg.backup_dir");
        //文件尾路径和文件名
        String tailStr = systemID + File.separator + instanceID + ".cfgcache";
        if (StringUtils.isEmpty(backupDir)) {
            // 默认从系统用户当前目录
            backupDir = System.getProperty("user.home") + File.separator + "cfgcache" + File.separator + tailStr;
        } else {
            backupDir += (backupDir.endsWith(File.separator) ? "" : File.separator) + tailStr;
        }
        return new File(backupDir);
    }
}

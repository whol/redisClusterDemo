package com.example.demo.config;

import org.apache.commons.lang3.StringUtils;

/**
 * 工厂加载类,配置信息读取,静态参数读取，数据连接
 *
 * @author 汤其奇
 * @date 2016-5-4
 */
public final class SysConfig {

    /**
     * zk连接地址
     */
    private static String zkAddress;

    /**
     * 系统名称
     */
    public static final String SYS_NAME;

    /**
     * 应用名称
     */
    public static final String APP_NAME;

    /**
     * 机器实例唯一编号
     */
    public static String INST_IP;
    /**
     * 机器实例唯一编号
     */
    public static final String INST_CODE;

    /**
     * 系统默认转码编码
     */
    public static final String BACKUP_DIR;

    /**
     * 消息用
     */
    public static final String MESSAGE_KEY;

    public static final String MESSAGE_ENCODING;

    /**
     * 节点数据截取长度
     */
    public static final int LEFT_TRIM_SIZE = 14;

    /**
     * 系统默认转码编码
     */
    public static final String ENCODING_UTF8 = "utf-8";

    /**
     * 是否初始化连接
     */
    private static boolean isModeInitialized = false;
    /**
     * 是否使用远程模式
     */
    private static boolean isRemote = false;

    /**
     * 私有构造函数
     */
    private SysConfig() {
    }

    // 使用java的静态块，进行存储一些从配置文件中获取的数据，只是在类被第一次实体化的时候才会被仅仅调用一次
    static {

        zkAddress = getBootProperty("cfg.zk.servers");
        SYS_NAME = getBootProperty("cmos.system.id");
        //cmos.system.id必须配置
        if (StringUtils.isEmpty(SYS_NAME)) {
            throw new IllegalArgumentException("cmos.system.id未配置");
        }
        APP_NAME = getBootProperty("cmos.app.id");
        INST_IP = InetAddressUtils.getLocalInetAddress();

        INST_CODE = getBootProperty("cmos.instance.id");
        //cmos.instance.id必须配置
        if (StringUtils.isEmpty(INST_CODE)) {
            throw new IllegalArgumentException("cmos.instance.id未配置");
        }

        BACKUP_DIR = LocalBackupUtils.getLocalBackupDir(SYS_NAME, INST_CODE).getAbsolutePath();
        MESSAGE_KEY = getBootProperty("cfg.message_key");
        MESSAGE_ENCODING = getBootProperty("cfg.message_encoding");
        INST_IP = InetAddressUtils.getLocalInetAddress();
    }

    public static boolean isModeInitialized() {
        return isModeInitialized;
    }

    public static void setIsModeInitialized(boolean isModeInitialized) {
        SysConfig.isModeInitialized = isModeInitialized;
    }

    /**
     * 判断是否是远程模式
     * 如果是 远程模式 返回 true
     * 如果是 本地模式 返回 false
     *
     * @return
     */
    public static boolean isRemoteMode() {
        return isRemote;
    }

    public static void setRemoteMode(boolean isRemote) {
        SysConfig.isRemote = isRemote;
    }

    public static String getZkAddress() {
        return zkAddress;
    }

    public static void setZookeeperServer(String zkAddress2) {
        zkAddress = zkAddress2;
    }

    @Deprecated
    public static void setZkAddress(String zkAddress) { SysConfig.setZookeeperServer(zkAddress); }

    /**
     * 获取启动配置
     * 1. 从 -D 参数获取
     * 2. 从环境变量获取
     *
     * @param key
     * @return
     */
    private static String getBootProperty(String key) {
        return System.getProperty(key);
    }

}
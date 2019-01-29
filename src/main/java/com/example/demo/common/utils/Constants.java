package com.example.demo.common.utils;

/**
 * <pre>
 * 全局配置项常量
 * - RE_前缀为正则式常量
 * - K_前经为配置项属性名常量
 * - DEFAULT_前缀为默认常量值
 * </pre>
 * Created by Zav Deng/dengzf@asiainfo.com on 17-3-7.
 */
public class Constants {

    //
    // 是否使用200成功返回，返回错误请求
    public static final String K_NORMAL_STATUS = "spring.webmvc.normal-error";

    public static final String K_DEFAULT_CHARSET = "spring.web.charsetName";

    public static final String K_APP_INITIALIZERS  = "spring.app.initializers";

    // 默认字符集编码
    public static final String DEFAULT_CHARSET = "utf-8";

    private static final String[] STATIC_RESOURCE_PATTERNS = {"*.json", "*.ttf", "*.eot", "*.woff", "*.svg"};

    // 分隔符正则式
    public static final String RE_DELIMERS = "\\s*[,;]\\s*";

    public static final String SESSION_CONFIG_FILE = "/session.properties";

    public static final String K_SESSION_redisCluster = "session.redisCluster";
    public static final String K_SESSION_maxRedirects = "session.redis.maxRedirects";
    public static final String K_SESSION_redisNodes = "session.redisNodes";
    public static final String K_JEDISPOOL_blockWhenExhausted = "session.jedispool.blockWhenExhausted";
    public static final String K_SPRING_SESSION_ENABLED = "spring.session.enabled";
    public static final String K_MANAGEMENT_ENABLED = "management.enabled";
    public static final String K_MSG_BROKERS = "msg.brokers";
    public static final String K_MSG_ENABLED = "msg.enabled";
    public static final String K_CFG_ENABLED = "cfg.enabled";
    public static final String K_CFG_ZK_SERVERS = "cfg.zk.servers";
    public static final String K_CFG_APPNAME = "cfg.appname";
    public static final String K_CFG_SYSNAME = "cfg.sysname";
    public static final String K_CFG_INSTNANCE_CODE = "cfg.instance.code";
    public static final String K_STATS_SERVICE_ENABLED = "stats.service.enabled";
    public static final String K_INSTANCE_ID = "cmos.instance.id";
    public static final String K_REMOTE_PACKAGES = "cmos.remote.packages";
    public static final String K_REMOTE_ENABLED = "cmos.remote.enabled";

    public static final int DEFAULT_SESSION_MAXREDIRECTS = 3;
    public static final int DEFAULT_JEDISPOOL_MAXIDLE = 5;
    public static final int DEFAULT_JEDISPOOL_MAXTOTAL = 5;
    public static final int DEFAULT_JEDISPOOL_MAXWAITMILLIS = 5000;

    public static final String DEFAULT_TIMEFORMAT = "yyyyMMddHHmmssSSS";

    public static final String CMOS_LOGGER_INTERCEPTOR = "com.cmos.core.logger.interceptor.spring.SpringBeanLoggerInterceptor";
    public static final String SPRING_PLUGIN_MONITOR_ACTUATOR = "org.springframework.boot.actuate.endpoint.Endpoint";
    public static final String SPRING_SECURITY_FILTER_CLASS = "org.springframework.security.web.DefaultSecurityFilterChain";
    public static final String COMMON_SERVICE_PACKAGE = "com.cmos.common.service";
    public static final String STATS_PACKAGES = "com.cmos.common.monitor";


    public static final String X_HEADER_TOKEN = "X-Token";
    public static final String X_HEADER_TRANSID = "X-Trans-Id";
    public static final String FLAG_PAGINATION = "_COUNT";

    // 配置平台初始入口类
    public static final String CLASS_CONFIG_CONTEXT_INITIALIZER = "com.cmos.cfg.support.ConfigContextInitializer";

    public static final String K_SYSTEM_ID = "cmos.system.id";

    public static final String K_APP_ID = "cmos.app.id";

    // 默认日期字符串格式
    public static final String DEFAULT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";


    /**
     * 存储当前登录用户id的字段名
     */
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";

    /**
     * token有效期（分钟） 12小时失效
     */
    public static final int TOKEN_EXPIRES_TIME = 60 * 12;

    /**
     * 存放Authorization的header字段
     */
    public static final String AUTHORIZATION = "authorization";

    /**
     * token生成的密钥
     */
    public static final String TOKEN_SECRET = "TOKEN_SECRET@2019010";

    /**
     * 缓存时间 秒
     */
    public static final int CACHE_TIME = 24*60*60;

    /**
     * 用户session
     */
    public static final String USER_SESSION = "USER_SESSION";

    /**
     * 用户类型session
     */
    public static final String USER_TYPE_SESSION = "USER_TYPE__SESSION";

    /**
     * 缓存标志定义
     */
    public interface USER_CACHE_FLAG {
        int USERINFO_EXPIRE_TIME = 3600;//秒，用户信息缓存时间1小时
        int USER_CHECKCODE_EXPIRE_TIME = 600;//秒，验证码有效时间10分钟
        int CACHE_EXPIRE_TIME = 24 * 60 * 60;//秒，缓存有效时间24小时
        String TOKEN = "ETW_NET:TOKEN_";//TOKEN秘钥
    }

    /**
     * 角色类型为0的数据，每个用户只有一个这种类型的角色
     */
    public interface ROLE_MAIN {
        String ROLE_GUEST = "300000";//游客
        String ROLE_PERSON = "300002";//个人客户

    }
}

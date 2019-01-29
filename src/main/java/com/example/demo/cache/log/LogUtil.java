package com.example.demo.cache.log;

import com.example.demo.cache.cacheException.entity.CustomException;
import com.example.demo.cache.util.IPUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gubaofeng
 * @Date 2017/8/17
 * @Time 14:33
 */
@Slf4j
public class LogUtil {

    /**
     * 自定义缓存异常对象list
     */
    private static List<CustomException> customExceptions;

    /**
     * 异常前缀
     */
    private static String cacheExceptionPrefix;

    /**
     * 写入日志平台
     *
     * @param e
     * @param key
     */
    public static void writeLog(String funcName, Exception e, String key, Class clazz) {
        CustomException customExceptionFound = findCustomException(e);
        Map logMap = buildLogMap(funcName, e, key, customExceptionFound);
        log.info("cachelog", logMap);
    }

    /**
     * 整理日志平台logMap
     *
     * @param e
     * @param key
     * @param customExceptionFound
     * @return
     */
    private static Map buildLogMap(String funcName, Exception e, String key, CustomException customExceptionFound) {
        Map logMap = new HashMap<String, String>();
        logMap.put("sysCode", System.getProperty("cmos.system.id", ""));
        logMap.put("key", key);
        logMap.put("funcName", funcName);
        ByteArrayOutputStream fullStackTrace = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(fullStackTrace));
        logMap.put("exceptionDetail", getCacheExceptionPrefix() + (e.getMessage() != null ? "-" + e.getMessage() + "\r\n" : "") + fullStackTrace.toString());
        logMap.put("collectTime", formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        logMap.put("clientIP", IPUtil.getRealIp());
        logMap.put("exceptionType", e.getClass().getName());
        logMap.put("priority", customExceptionFound != null ? customExceptionFound.getPriority() : 99);
        return logMap;
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param format
     * @return
     */
    private static String formatDate(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * 根据异常名称，查找对应的异常缓存对象
     *
     * @param e
     * @return
     */
    private static CustomException findCustomException(Exception e) {
        String exceptionName = e.getClass().getName();
        CustomException customExceptionFound = null;
        if (customExceptions != null)
            for (CustomException customException :
                    customExceptions) {
                if (exceptionName.contains(customException.getName())) {
                    customExceptionFound = customException;
                    break;
                }
            }
        return customExceptionFound;
    }

    public static List<CustomException> getCustomExceptions() {
        return customExceptions;
    }

    public static void setCustomExceptions(List<CustomException> customExceptions) {
        LogUtil.customExceptions = customExceptions;
    }

    public static String getCacheExceptionPrefix() {
        return cacheExceptionPrefix;
    }

    public static void setCacheExceptionPrefix(String cacheExceptionPrefix) {
        LogUtil.cacheExceptionPrefix = cacheExceptionPrefix;
    }
}

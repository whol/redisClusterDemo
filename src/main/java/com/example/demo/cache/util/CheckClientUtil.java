package com.example.demo.cache.util;

import com.example.demo.cache.cacheException.CMOSCacheBaseException;
import com.example.demo.cache.cacheException.ValueEmptyException;
import com.example.demo.cache.cacheException.ValueSizeBigException;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

/**
 * @author gubaofeng
 * @Date 2017/7/17
 * @Time 19:14
 */
public class CheckClientUtil {
    private static final int VALUE_MAX_SIZE = 5120;

    private static boolean checkValueIsBig = false;

    private static boolean checkValueisEmpty = false;

    public static void setCheckValueIsBig(boolean checkValueIsBig) {
        CheckClientUtil.checkValueIsBig = checkValueIsBig;
    }

    public static void setCheckValueisEmpty(boolean checkValueisEmpty) {
        CheckClientUtil.checkValueisEmpty = checkValueisEmpty;
    }

    /**
     * 判断是否为空、判断大小
     *
     * @param value
     */
    public static void isEmptyOrBig(Object value) {
        isEmpty(value);
        isBig(value);
    }

    /**
     * 判断是否为空、判断大小
     *
     * @param value
     */
    public static void isEmptyOrBig(byte[] value) {
        isEmpty(value);
        isBig(value);
    }

    /**
     * 判断是否为空、判断大小
     *
     * @param value
     */
    public static void isEmptyOrBig(String value) {
        isEmpty(value);
        isBig(value);
    }

    /**
     * 判断大小，判断value的size，针对具体的类型，方法重载
     * (大小不能超过5k)
     *
     * @param value
     * @return
     */
    private static void isBig(String value) {
        if (checkValueIsBig && value.length() >= VALUE_MAX_SIZE) {
            throw new CMOSCacheBaseException(new ValueSizeBigException("value大小不符合规则"));
        }
    }

    /**
     * 判断是否为空
     *
     * @param value
     * @return
     */
    private static void isEmpty(String value) {
        if (checkValueisEmpty && StringUtils.isEmpty(value)) {
            throw new CMOSCacheBaseException(new ValueEmptyException("value不能为空"));
        }
    }

    /**
     * 判断大小，判断value的size，针对具体的类型，方法重载
     * (大小不能超过5k)
     *
     * @param value
     * @return
     */
    private static void isBig(byte[] value) {
        if (checkValueIsBig && value.length >= VALUE_MAX_SIZE) {
            throw new CMOSCacheBaseException(new ValueSizeBigException("value大小不符合规则"));
        }
    }

    /**
     * 判断是否为空
     *
     * @param value
     * @return
     */
    private static void isEmpty(byte[] value) {
        if (checkValueisEmpty && (null == value || value.length == 0)) {
            throw new CMOSCacheBaseException(new ValueEmptyException("value不能为空"));
        }
    }

    /**
     * 判断大小，判断value的size，针对具体的类型，方法重载
     * (大小不能超过5k)
     *
     * @param value
     * @return
     */
    private static void isBig(Object value) {
        if (checkValueIsBig) {
            byte[] setvalue = SerializationUtils.serialize(value);
            if (setvalue.length >= VALUE_MAX_SIZE) {
                throw new CMOSCacheBaseException(new ValueSizeBigException("value大小不符合规则"));
            }
        }
    }

    /**
     * 判断是否为空
     *
     * @param value
     * @return
     */

    private static void isEmpty(Object value) {
        if (checkValueisEmpty && value == null) {
            throw new CMOSCacheBaseException(new ValueEmptyException("value不能为空"));
        }
    }
}
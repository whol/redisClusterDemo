package com.example.demo.common.utils;

import com.cmos.common.exception.IOResourceException;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

/**
 * 类型相关操作工具类
 *
 * Created by Zav Deng/dengzf@asiainfo.com on 17-3-6.
 */
public class TypeUtils {
    
    private static final Class<?>[] JSON_PrimitiveTypes = {
            String.class, int.class, Integer.class, Date.class, Time.class, Timestamp.class,
            long.class ,Long.class ,Character.class ,char.class ,short.class ,Short.class
            ,Byte.class ,byte.class ,Double.class ,double.class ,float.class,Float.class,BigInteger.class
            ,BigDecimal.class,StringBuffer.class,StringBuilder.class,Boolean.class ,boolean.class
    };

    private static final Class<?>[] INTEGER_TYPES = {
            long.class,
            Long.class,
            int.class,
            Integer.class,
            Short.class,
            short.class
    };

    public static boolean isArrayType(Class<?> type) {
        return type.isArray();
    }

    public static boolean isArrayObject(Object object) {
        return object != null && object.getClass().isArray();
    }

    /**
     * 判断一个类型是否为原生类型
     * @param type 待检测类型
     * */
    public static boolean isPrimitiveType(Class<?> type) {
        return  isJSONPrimitiveType(type) || isThrowableType(type);
    }

    /**
     * 判断指定类型是否为JSON的原生类型，不需要进行深度分析转化
     * @param type
     * */
    public static boolean isJSONPrimitiveType(Class<?> type) {
        for (Class<?> primitiveType : JSON_PrimitiveTypes) {
            if (primitiveType == type)
                return true;
        }
        return false;
    }

    /**
     * 判断指定类型是否为异常类Throwable的衍生类型
     * @param type
     * */
    public static boolean isThrowableType(Class<?> type) {
        Class<?> errorClass = type.getSuperclass();
        while (errorClass != null) {
            if (errorClass == Throwable.class)
                return true;
            errorClass = errorClass.getSuperclass();
        }
        return false;
    }

    public static boolean containsInterface(Class<?> targetType, Class<?>... interfaceTypes) {
        Class<?>[] superInterfaces = targetType.getInterfaces();
        for (Class<?> superInterface : superInterfaces) {
            for (Class<?> checkType : interfaceTypes) {
                if (checkType == superInterface)
                    return true;
            }
        }
        return false;
    }

    /**
     * 根据指定的参数值的类型，获取类型的指定构造器，创建类型的实例
     * @param objectType
     * @param args
     * */
    public static <T> T newInstance(Class<T> objectType, Object ... args) {
        Class<?>[] types = constructTypes(args);
        try {
            Constructor<T> constructor = objectType.getConstructor(types);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot create instance with arguments: " + Arrays.toString(types) + ", " + e.getMessage(), e);
        }
    }

    private static Class<?>[] constructTypes(Object[] args) {
        int index = 0;
        Class<?>[] types = new Class<?>[args.length];
        for (Object arg : args) {
            if (arg == null)
                throw new IllegalArgumentException("arguments continas null value");
            types[index++] = args.getClass();
        }
        return types;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Throwable.class.getSuperclass());
        System.out.println(Exception.class.getSuperclass());
        System.out.println(IOResourceException.class.getSuperclass());
        System.out.println(IOResourceException.class.getSuperclass().getSuperclass().getSuperclass());
        System.out.println(TypeUtils.isPrimitiveType(IOResourceException.class));
        System.out.println(TypeUtils.isPrimitiveType(Class.class));
    }

    public static boolean classExists(String typeClass) {
        try {
            Class.forName(typeClass);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isIntegerType(Object result) {
        for (Class<?> type : INTEGER_TYPES) {
            if (result.getClass() == type)
                return true;
        }
        return false;
    }
}

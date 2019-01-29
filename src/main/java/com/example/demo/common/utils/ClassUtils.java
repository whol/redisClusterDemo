package com.example.demo.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-10-26.
 */
public class ClassUtils {


    /**
     * 获取对象类型的所有实例域的字段
     * @param typeClass 对象类型
     *
     * */
    public static List<Field> getDeclaredFields(Class<?> typeClass) {
        return getDeclaredFields(typeClass, true);
    }

    /**
     * 获取对象类型的所有实例域的字段
     *
     * @param typeClass 对象类型
     * @param inheritFields 是否获取继承的字段
     * */
    public static List<Field> getDeclaredFields(Class<?> typeClass, boolean inheritFields) {
        return _getDeclaredFields(new ArrayList<Field>(), typeClass, inheritFields);
    }

    private static List<Field> _getDeclaredFields(List<Field> fieldList, Class<?> typeClass, boolean inheritFields) {
        if (typeClass == null) {
            return fieldList;
        } else {
            for (Field field : typeClass.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (!Modifier.isStatic(modifiers)
                        && !field.getName().startsWith("this$")) {
                    fieldList.add(field);
                }
            }
            return inheritFields ? _getDeclaredFields(fieldList, typeClass.getSuperclass(), inheritFields) : fieldList;
        }
    }

    public static List<Method> getDeclaredMethods(Class<?> typeClass) {
        return _getDeclaredMethods(new ArrayList<Method>(), typeClass, true);
    }

    private static List<Method> _getDeclaredMethods(List<Method> methodList, Class<?> typeClass, boolean inheritMethods) {
        if (typeClass == null) {
            return methodList;
        } else {
            for (Method method : typeClass.getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                if (!Modifier.isStatic(modifiers)) {
                    methodList.add(method);
                }
            }
            return inheritMethods ? _getDeclaredMethods(methodList, typeClass.getSuperclass(), inheritMethods) : methodList;
        }
    }

	/**
	 * 通过获取当前调用栈，找到入口方法main所在的类
	 * 
	 * @return
	 */
	public static Class<?> getMainApplicationClass() {
		try {
			StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				if ("main".equals(stackTraceElement.getMethodName())) {
					return Class.forName(stackTraceElement.getClassName());
				}
			}
		} catch (ClassNotFoundException ex) {
			// Swallow and continue
		}
		return null;
	}

}

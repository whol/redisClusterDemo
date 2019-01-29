package com.example.demo.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记注解的方法需要进行登陆验证
 */
@Target({ElementType.METHOD})//可用在方法名上
@Retention(RetentionPolicy.RUNTIME)//运行时有效
public @interface LoginRequired {
}

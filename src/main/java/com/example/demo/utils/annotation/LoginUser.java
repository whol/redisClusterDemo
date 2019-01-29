package com.example.demo.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登陆用户信息
 */
@Target({ElementType.METHOD})//可用在方法名上
@Retention(RetentionPolicy.RUNTIME)//运行时有效
public @interface LoginUser {
}

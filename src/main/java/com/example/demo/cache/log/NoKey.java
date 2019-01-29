package com.example.demo.cache.log;

import java.lang.annotation.*;

/**
 * 没有key
 * @author gubaofeng
 * @Date 2017/8/15
 * @Time 18:17
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface NoKey {
}

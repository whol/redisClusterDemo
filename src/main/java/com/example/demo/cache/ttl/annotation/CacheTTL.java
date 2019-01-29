package com.example.demo.cache.ttl.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * Spring cache 缓存生存时间注解
 * @author zhuanghd
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheTTL {

    /**
     * 生存时间的时间单位
     */
    TimeUnit unit();

    /**
     * 生存时间,默认值为-1表示不设置失效
     */
    long time();

}


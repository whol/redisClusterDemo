package com.example.demo.cache.route.annotation;

import java.lang.annotation.*;


/**
 * Spring cache 缓存路由注解
 * @author zhuanghd
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheRoute {

    // 路由规则名称 --- 对应 redis.properties 配置文件中的路由名称
    String name() default "";

    // 路由参数,是一个具体的值或者是变量
    // 与Cacheable、CachePut、CacheEvict注解的 key 的规则一致
    String param() default "";

}


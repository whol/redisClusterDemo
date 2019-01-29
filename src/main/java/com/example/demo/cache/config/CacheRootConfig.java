package com.example.demo.cache.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

@Order(1)
@Configuration
@ComponentScan("com.example.demo.cache")
@EnableAspectJAutoProxy
/**
 * cache-cache 组件配置
 * @author liuyan
 * @since 1.0
 */
public class CacheRootConfig {
}

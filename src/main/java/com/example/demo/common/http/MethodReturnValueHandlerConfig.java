package com.example.demo.common.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Http返回值配置
 *
 * @author zhuanghd
 * @see java.io...
 * @since 1.0
 */
@Configuration
public class MethodReturnValueHandlerConfig {

    @Bean
    public MethodReturnValueHandlerFactoryBean methodReturnValueHandlerFactoryBean() {
        return  new MethodReturnValueHandlerFactoryBean();
    }

}

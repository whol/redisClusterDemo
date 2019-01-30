package com.example.demo.common.exception.autoconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 统一异常初始化入口
 *
 * @author Zifeng.D 2016-12-02 创建
 * @since 0.0.1
 */
@Configuration()
//@ComponentScan(basePackages = "com.cmos.common.exception.*")
@PropertySource(value = "exception", name="exception", factory = GeneralExceptionPropertySourceFactory.class)
@Slf4j
public class GeneralExceptionAutoConfig {

    public GeneralExceptionAutoConfig() {
        log.info("统一异常处理模块配置初始化...");
    }

}

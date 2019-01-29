package com.example.demo.common.exception.autoconfig;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

/**
 * 错误码配置源工厂类
 *
 * @author Zifeng.D 2016-12-02 创建
 * @since 0.0.1
 */
public class GeneralExceptionPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        return new GeneralExceptionPropertySource(name);
    }
}

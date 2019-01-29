package com.example.demo.common.exception.autoconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 统一异常模块导入注解
 * <p>
 *     用法示例:
 *     @EnableGeneralException
 *     public class Application {
 *
 *     }
 * </p>
 *
 * @author Zifeng.D 2016-12-02 创建
 * @since 0.0.1
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.TYPE })
@Documented
@Import(GeneralExceptionAutoConfig.class)
@Configuration
public @interface EnableGeneralException {

}

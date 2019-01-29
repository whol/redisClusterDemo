package com.example.demo.common.annotation;

import com.example.demo.common.http.MethodReturnValueHandlerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by alen on 17-3-2.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MethodReturnValueHandlerConfig.class})
public @interface EnableReturnValueHandler {

}

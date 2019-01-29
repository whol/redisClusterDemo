package com.example.demo.common.annotation;

import java.lang.annotation.*;

/**
 * Created by alen on 17-2-7.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface CompatibleOutput {

}

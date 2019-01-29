package com.example.demo.common.utils;


import java.lang.annotation.*;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-4-14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ResultFormatter {

    Class<? extends ValueFormatter> formatterClass();
}

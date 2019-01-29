package com.example.demo.common.json;

import java.lang.annotation.*;

import static com.example.demo.common.utils.Constants.DEFAULT_DATE_FORMAT;


/**
 * Created by Zav Deng on 17-11-2.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface JsonField {

    String[] name() default {};

    Class<?>[] elementType() default {};

    String dateFormat() default DEFAULT_DATE_FORMAT;

    String locale() default "en";
}

package com.example.demo.utils.annotation;

import com.example.demo.common.utils.DummyValueFormatter;
import com.example.demo.common.utils.ValueFormatter;

import java.lang.annotation.*;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-9-18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface JsonifyStrategy {

    int strategy();

    Class<? extends ValueFormatter> formatter() default DummyValueFormatter.class;

}

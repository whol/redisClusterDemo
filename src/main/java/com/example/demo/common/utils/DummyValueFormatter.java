package com.example.demo.common.utils;


/**
 * 默认JSON格式化值转换类
 *
 * Created by Zav Deng/dengzf@asiainfo.com on 17-4-13.
 */
public class DummyValueFormatter implements ValueFormatter {
    @Override
    public Object format(String key, Object value) {
        if (value instanceof Throwable)
            return value.getClass() + ": " + ((Throwable) value).getMessage();
        return value;
    }
}

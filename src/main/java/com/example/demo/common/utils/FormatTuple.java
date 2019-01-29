package com.example.demo.common.utils;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-10-28.
 */
public class FormatTuple {

    private String key;
    private Object value;

    public FormatTuple(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}

package com.example.demo.common.utils;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-4-13.
 */
public interface ValueFormatter {

    /***
     * 提供对一个JSON对象每个键值对象
     * */
    Object format(String key, Object value);

}

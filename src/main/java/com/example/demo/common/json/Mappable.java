package com.example.demo.common.json;

/**
 * Created by Zav Deng on 17-11-3.
 */
public interface Mappable<T> {

    T get(String key);

    boolean contains(String key);

}

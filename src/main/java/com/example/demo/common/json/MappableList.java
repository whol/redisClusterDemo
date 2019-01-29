package com.example.demo.common.json;

/**
 * Created by Zav Deng 17-11-3.
 */
public interface MappableList<T> {

    T get(int index);

    int length();

}

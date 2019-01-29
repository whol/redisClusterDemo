package com.example.demo.common.collections;

/**
 * Created by alen on 17-9-21.
 */
public class DataItem<V> extends Tuple<String, V> {

    public static final <V> DataItem<V> of(String key, V value) {
        return new DataItem<>(key, value);
    }

    DataItem(String first, V second) {
        super(first, second);
    }

    public V getValue() { return this.getSecond(); }

    public String getKey() { return this.getFirst(); }

}

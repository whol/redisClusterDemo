package com.example.demo.common.json;

/**
 * Created by alen on 17-11-2.
 */
public class MappedPair {

    private String name;
    private Object value;
    private boolean multipart;

    public MappedPair(String name, Object value) {
        this(name, value, false);
    }

    public MappedPair(String name, Object value, boolean multipart) {
        this.name = name;
        this.value = value;
        this.multipart = multipart;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isMultipart() {
        return multipart;
    }
}

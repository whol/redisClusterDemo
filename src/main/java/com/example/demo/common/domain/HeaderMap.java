package com.example.demo.common.domain;

import com.example.demo.common.collections.DataMap;

/**
 * Created by alen on 17-10-12.
 */
public class HeaderMap extends DataMap<HeaderValues> {

    public String getFirst(String name) {
        if (containsKey(name)) {
            HeaderValues headerValues = get(name);
            return headerValues.first();
        }
        return "";
    }

}

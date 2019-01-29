package com.example.demo.common.domain;

import java.util.ArrayList;

/**
 * Created by alen on 17-10-12.
 */
public class HeaderValues extends ArrayList<String> {

    public String first() {
        return this.size() > 0 ? this.get(0) : "";
    }

}

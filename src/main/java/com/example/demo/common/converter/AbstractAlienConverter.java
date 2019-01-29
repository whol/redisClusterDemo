package com.example.demo.common.converter;

import com.example.demo.common.utils.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-8-9.
 */
public abstract class AbstractAlienConverter implements AlienConverter {

    protected Object fromJSONString(String value) {
        if (JSONUtils.isJSONString(value)) {
            return value.startsWith("[") ?  new JSONArray(value) : new JSONObject(value);
        } else {
            return value;
        }
    }

}

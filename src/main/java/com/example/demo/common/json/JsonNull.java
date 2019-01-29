package com.example.demo.common.json;

import org.json.JSONObject;

/**
 * Created by Zav Deng on 17-11-3.
 */
public class JsonNull extends JsonValue<Object> {

    protected JsonNull() {
        super(JSONObject.NULL);
    }
}

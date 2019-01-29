package com.example.demo.common.json;

import org.apache.http.util.Asserts;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Zav Deng on 17-10-16.
 */
public abstract class JsonValue<T> {

    public static final JsonNull NULL = new JsonNull();

    private T value;

    public JsonValue(T value) {
        Asserts.notNull(value, "value can not be null");
        this.value = value;
    }

    public boolean isNull() {
        return getValue() == JSONObject.NULL;
    }

    public Date toDate(String format, Locale locale) throws ParseException {
        if (!(this instanceof JsonString)) {
            throw new UnsupportedOperationException("No date conversion for type: " + this.getClass());
        }
        return new SimpleDateFormat(format, locale).parse(this.getValue().toString());
    }

    public T getValue() {
        return value;
    }

    protected JsonValue<?> convert(Object value) {
        if (value == null) {
            return JsonValue.NULL;
        } else if (value instanceof JsonValue) {
            return (JsonValue)value;
        } if (value instanceof JSONArray) {
            return new JsonArray((JSONArray) value);
        } else if (value instanceof JSONObject) {
            return new JsonObject((JSONObject) value);
        } else if (value instanceof String) {
            return new JsonString((String)value);
        } else if (value instanceof Integer
                || value instanceof Long
                || value instanceof Double
                || value instanceof Float
                || value instanceof BigInteger
                || value instanceof BigDecimal) {
            return new JsonNumber(value);
        } else {
            return new JsonAny(value);
        }
    }

}

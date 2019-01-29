package com.example.demo.common.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-4-17.
 */
public abstract class JSONFormatter {

    public static final Object format(Object value) {
        return format(value, new DummyValueFormatter());
    }

    public static final Object format(Object value, ValueFormatter formatter) {
        return format(value, "ROOT", formatter);
    }

    public static final Object format(Object value, String key, ValueFormatter formatter) {
        if (value == null || value == JSONObject.NULL || TypeUtils.isPrimitiveType(value.getClass())) {
            if (formatter != null && !(formatter instanceof DummyValueFormatter)) {
                value = formatter.format(key, value);
            }
            return value == null ? JSONObject.NULL : value;
        } else {
            return formatObject(value, key, formatter);
        }
    }

    private static Object formatObject(Object value, String key, ValueFormatter formatter) {
        if (value instanceof JSONArray) {
            return formatJSONArray((JSONArray) value, key, formatter);
        }  else if (value instanceof JSONObject) {
            return formatJSONObject((JSONObject)value, formatter);
        }else if (value instanceof Map) {
            return formatMap((Map) value, new JSONObject(), formatter);
        } else if (value instanceof Collection) {
            return formatCollection((Collection<?>)value, new JSONArray(), key, formatter);
        } else if (TypeUtils.isArrayObject(value)) {
            return formatArray(value, new JSONArray(), key, formatter);
        } else {
            return formatBean(value, new JSONObject(), formatter);
        }
    }

    private static Object formatJSONArray(JSONArray jsonArray, String key, ValueFormatter formatter) {
        int count = jsonArray.length();

        for (int i = 0 ; i < count; i++) {
            String itemKey = key + ":" + i;
            Object value = format(jsonArray.get(i), itemKey, formatter);
            if (value instanceof FormatTuple) {
                value = ((FormatTuple) value).getValue();
            }
            jsonArray.put(i, value);
        }
        return jsonArray;
    }

    private static Object formatJSONObject(JSONObject jsonObject, ValueFormatter formatter) {
        for (String key : jsonObject.keySet()) {
            Object value = format(jsonObject.get(key), key, formatter);
            updateObject(jsonObject, key, value);
        }
        return jsonObject;
    }

    private static Object formatArray(Object arrayObj, JSONArray jsonArray, String key, ValueFormatter formatter) {
        int count = Array.getLength(arrayObj);
        for (int i = 0 ; i < count; i++) {
            String itemKey = key + ":" + i;
            Object value   = Array.get(arrayObj, i);
            value = format(value, itemKey, formatter);
            if (value instanceof FormatTuple) {
                value = ((FormatTuple) value).getValue();
            }
            jsonArray.put(value);
        }
        return jsonArray;
    }

    private static Object formatCollection(Collection<?> objectList, JSONArray jsonArray, String key, ValueFormatter formatter) {
        int i = 0;
        for (Object value : objectList) {
            String itemKey = key + ":" + (i++);
            value = format(value, itemKey, formatter);
            if (value instanceof FormatTuple) {
                value = ((FormatTuple) value).getValue();
            }
            jsonArray.put(value);
        }
        return jsonArray;
    }

    private static Object formatMap(Map objectMap, JSONObject jsonObject, ValueFormatter formatter) {
        for (Object keyObj : objectMap.keySet()) {
            String key   = keyObj.toString();
            Object value = objectMap.get(keyObj);
            value = format(value, key, formatter);
            updateObject(jsonObject, key, value);
        }
        return jsonObject;
    }

    private static Object formatBean(Object bean, JSONObject jsonObject, ValueFormatter formatter) {
        PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(bean);
        for (PropertyDescriptor descriptor : properties) {
            String key   = descriptor.getName();
            try {
                Object value = PropertyUtils.getProperty(bean, key);
                if (value.getClass() != Class.class) {
                    value = format(value, key, formatter);
                    updateObject(jsonObject, key, value);
                }
            } catch (Exception e) {
                // ignore value;
            } }
        return jsonObject;
    }

    private static void updateObject(JSONObject jsonObject, String key, Object value) {
        if (value instanceof FormatTuple) {
            FormatTuple tuple = (FormatTuple)value;
            value = tuple.getValue();
            key = tuple.getKey();
        }
        jsonObject.put(key, value == null ? JSONObject.NULL : value);
    }

}

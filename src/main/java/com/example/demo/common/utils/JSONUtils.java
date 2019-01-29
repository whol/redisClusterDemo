package com.example.demo.common.utils;

import com.example.demo.beans.common.GenericBean;
import com.example.demo.beans.common.JsonBean;
import com.example.demo.beans.common.JsonConvertException;
import com.example.demo.common.collections.DataSet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * JSON工具类
 * Created by Zav Deng/dengzf@asiainfo.com on 17-3-2.
 */
public class JSONUtils {

    private static final Pattern jsonPattern = Pattern.compile("^(\\{.*\\}|\\[.*\\])$", Pattern.MULTILINE | Pattern.DOTALL);

    /***
     * 将JSON字符串转为JSON对象
     *
     * @param jsonString JSON字符串
     * @param type JSON类型，有效值：JSONObject.class/JSONArray.class
     * */
    public static <T> T parseJSON(String jsonString, Class<T> type) {
        if (type == JSONObject.class)
            return (T)new JSONObject(jsonString);
        else if (type == JSONArray.class)
            return (T)new JSONArray(jsonString);
        else
            throw new UnsupportedOperationException("Unknown source type: " + type.getName());
    }

    /**
     * 判断一个字符串是否为JSON格式的字符串
     * @param source 源字符串
     * */
    public static boolean isJSONString(String source) {
        return jsonPattern.matcher(source).matches();
    }

    public static JSONObject toJSONObject(Object obj) {
        return (JSONObject) toJSON(obj);
    }

    /**
     * 将Java对象，集合或数组对象转换为JSONArray/JONObject对象
     * @param obj 可迭代的集合对象
     * @return 返回JSONArray或JSONObject对象
     * */
    public static Object toJSON(Object obj) {
        return JSONFormatter.format(obj, new DummyValueFormatter());
    }

    public static <T extends JsonBean<?>> Object toJSON(Object obj, ValueFormatter valueFormatter) {
        return JSONFormatter.format(obj, valueFormatter);
    }

    public static <T extends JsonBean<?>> T toJavaBean(JSONObject obj, Class<T> typeClass) throws JsonConvertException {
        return GenericBean.fromJSON(obj, typeClass);
    }

    public static <T extends JsonBean<?>> DataSet<T> toDataSet(JSONArray arr, Class<T> typeClass) throws JsonConvertException {
        return DataSet.fromJSON(arr, typeClass);
    }
}

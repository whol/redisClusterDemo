package com.example.demo.beans.common;


/**
 * <pre>
 * JSON单元实体类接口
 * 1. 提供实体转JSONObject/JSONArray的接口方法: com.cmos.common.bean.JsonBean#toJSON()
 * 2. 提供实体转JSON字符串的接口方法：com.cmos.common.bean.JsonBean#toJSONString()
 * </pre>
 *
 * Created by Zav Deng/dengzf@asiainfo.com on 17-9-18.
 * @see JsonObjectBean
 * @see JsonArrayBean
 * @see GenericBean
 */
public interface JsonBean<T> {

    /**
     * 将当前实体类转换为JSONArray/JSONObject
     * @see GenericBean#toJSON()
     * */
    T toJSON() throws JsonFormatException;

    /**
     * 将当前实体类转换为JSON字符串
     * @see GenericBean#toJSONString()
     * */
    String toJSONString() throws JsonFormatException;
}

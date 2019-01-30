package com.example.demo.common.converter;

import com.example.demo.common.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-8-9.
 */
@Slf4j
public class FieldDependentJsonConverter extends AbstractAlienConverter {

    private static final Pattern jsonPattern = Pattern.compile("^(\\{.+\\}|\\[.*\\])$", Pattern.MULTILINE | Pattern.DOTALL);

    @Override
    public Object convert(Object alienObj) {
        JSONObject jsonObject = new JSONObject();
        Field[] fields = alienObj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(alienObj);
                packJSONField(field.getName(), value, jsonObject);
            } catch (IllegalAccessException e) {
                log.warn("access field error: " + field.getName(), e);
            } finally {
                field.setAccessible(false);
            }
        }
        return jsonObject;
    }

    private void packJSONField(String name, Object value, JSONObject holder) {
        if (value != null) {
            if (value instanceof String) {
                holder.put(name, fromJSONString(((String) value).trim()));
            } else {
                holder.put(name, JSONUtils.toJSON(value));
            }
        }
    }





}

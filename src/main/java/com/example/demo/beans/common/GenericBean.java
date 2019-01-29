package com.example.demo.beans.common;


import com.example.demo.common.annotation.JsonOmit;
import com.example.demo.common.annotation.JsonifyStrategy;
import com.example.demo.common.collections.DataMap;
import com.example.demo.common.utils.ClassUtils;
import com.example.demo.common.utils.DummyValueFormatter;
import com.example.demo.common.utils.ValueFormatter;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * 所有非集合类单元实体的基础类
 *
 * Created by Zav Deng/dengzf@asiainfo.com on 17-9-18.
 */
public abstract class GenericBean implements JsonObjectBean, Serializable {

    public static final int STRATEGY_FIELD = 0x0000;

    public static final int STRATEGY_GETTER = 0x0001;

    public static final <T extends JsonBean<?>> T fromJSON(JSONObject jsonObject, Class<T> typeClass) throws JsonConvertException {
        return fromJSON(jsonObject, typeClass, null);
    }

    public static final <T extends JsonBean<?>> T fromJSON(JSONObject jsonObject, Class<T> typeClass, ValueFormatter formatter) throws JsonConvertException {
        if (jsonObject == null)
            throw new IllegalArgumentException("jsonObject参数不能空");

        try {
            JsonBean<?> instance = typeClass == null ? new DataMap<>() : typeClass.newInstance();
            if (instance instanceof DataMap) {
                for (Object propertyName : jsonObject.keySet()) {
                    Object value = jsonObject.get(propertyName.toString());
                    ((DataMap) instance).put(propertyName, value);
                }
            } else {
                Field[] fields = typeClass.getDeclaredFields();
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers())
                            || !jsonObject.has(field.getName())) {
                        continue;
                    }

                    if (!field.isAccessible())
                        field.setAccessible(true);

                    String typeName = field.getType().getSimpleName();

                    Method _obtainValueMethod = getObtainValueMethod(jsonObject, typeName);
                    if (_obtainValueMethod != null) {
                        field.set(instance, _obtainValueMethod.invoke(jsonObject, field.getName()));
                    } else {
                        // FIXME: 未找到对应的数据类型方法，执行智能转换设置字段值
                    }
                }
            }
            return (T)instance;
        } catch (Exception e) {
            throw new JsonConvertException("CM102", e);
        }
    }

    private static Method getObtainValueMethod(JSONObject jsonObject, String typeName) {
        try {
            String getter = "get" + Character.toUpperCase(typeName.charAt(0)) + typeName.substring(1);
            return jsonObject.getClass().getMethod(getter, String.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Override
    public JSONObject toJSON() throws JsonFormatException {
        Class<? extends GenericBean> klass = this.getClass();

        JsonifyStrategy jsonifyStrategy = klass.getAnnotation(JsonifyStrategy.class);

        ValueFormatter valueFormatter = getJsonValueFormatter(jsonifyStrategy);

        int strategy = GenericBean.STRATEGY_FIELD;
        if (jsonifyStrategy != null) {
            strategy = jsonifyStrategy.strategy();
        }

        JSONObject objectMap = new JSONObject();

        switch (strategy) {
            case STRATEGY_FIELD: _formatFields(valueFormatter, objectMap); break;
            case STRATEGY_GETTER: _formatGetters(valueFormatter, objectMap); break;
            default: throw new IllegalArgumentException("Unknown format strategy: " + jsonifyStrategy);
        }

        return objectMap;
    }

    private ValueFormatter getJsonValueFormatter(JsonifyStrategy jsonifyStrategy) {
        return new DummyValueFormatter();
    }

    private void _formatGetters(ValueFormatter valueFormatter, JSONObject objectMap) throws JsonFormatException {
        // 获取类自身定义的方法
        Method[]  methods = this.getClass().getDeclaredMethods();

        for (Method method : methods) {

            if (method.getAnnotation(JsonOmit.class) != null)
                continue;

            if (!method.getName().startsWith("get")
                    && method.getParameterTypes().length > 0) {
                // 跳过不是通用的getter方法
                continue;
            }

            if (!Modifier.isPublic(method.getModifiers())
                    || Modifier.isStatic(method.getModifiers())) {
                // 跳过非public或为static的方法
                continue;
            }

            try {
                objectMap.put(getPropertyName(method.getName()), formatValue(method.invoke(this)));
            } catch (Exception e) {
                throw new JsonFormatException("CM100", e);
            }
        }
    }

    private String getPropertyName(String name) {
        return name.replaceAll("^get", "");
    }

    private Object formatValue(Object value) throws JsonFormatException {
        if (value == null) {
            return JSONObject.NULL;
        } else if (value instanceof JsonBean) {
            return ((JsonBean) value).toJSON();
        } else {
            return value;
        }
    }

    private void _formatFields(ValueFormatter valueFormatter, JSONObject objectMap) throws JsonFormatException {
        // 获取类自身定义的方法
        List<Field> fields = ClassUtils.getDeclaredFields(this.getClass());

        for (Field field : fields) {
            if (field.getAnnotation(JsonOmit.class) == null) {
                try {
                    field.setAccessible(true);
                    objectMap.put(getPropertyName(field.getName()), formatValue(field.get(this)));
                } catch (Exception e) {
                    throw new JsonFormatException("CM100", e);
                }
            }
        }
    }

    @Override
    public String toJSONString() throws JsonFormatException {
        return toJSON().toString();
    }

}
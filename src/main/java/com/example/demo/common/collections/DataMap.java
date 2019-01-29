package com.example.demo.common.collections;

import com.example.demo.beans.common.GenericBean;
import com.example.demo.beans.common.JsonBean;
import com.example.demo.beans.common.JsonFormatException;
import com.example.demo.beans.common.JsonObjectBean;
import com.example.demo.common.monad.Option;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-9-18.
 */
public class DataMap<E> extends HashMap<String, E> implements JsonObjectBean, Serializable {

    public static final DataMap<?> emptyMap = new DataMap<>();

    public static final <E> DataMap<E> make(DataItem<E> ... entries) {
        final DataMap<E> dataMap = new DataMap<>();
        for (DataItem<E> entry : entries) {
            dataMap.put(entry.getKey(), entry.getValue());
        }
        return dataMap;
    }

    public static final DataMap<?> of(Object bean) {
        return of(bean, false);
    }

    public static final DataMap<?> of(Object bean, boolean deepConvert) {
        if (bean == null)
            return DataMap.emptyMap;
        else {
            final DataMap dataMap = new DataMap<>();
            final Field[] fields = bean.getClass().getFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(bean);
                    if (field.getType() == GenericBean.class) {
                        dataMap.put(field.getName(), deepConvert ? value : DataMap.of(value, deepConvert));
                    } else {
                        dataMap.put(field.getName(), value);
                    }
                } catch (Exception e) {
                    // ignore format exception
                }
            }
            return dataMap;
        }
    }

    public <T> T get(String key) {
        return (T)super.get(key);
    }

    public <T> DataItem<T> getItem(String key) {
        return DataItem.of(key, (T)get(key));
    }

    public Iterator<String> keyIterator() {
        return this.keySet().iterator();
    }

    public Iterator<E> valueIterator() {
        return this.values().iterator();
    }

    public Option<E> safeGet(String key) {
        return Option.of(super.get(key));
    }

    @Override
    public JSONObject toJSON() throws JsonFormatException {
        JSONObject objectMap = new JSONObject();

        Set<String> keys = this.keySet();
        for (String key : keys) {
            E value = get(key);
            if (value == null) {
                objectMap.put(key, JSONObject.NULL);
            } else if (value instanceof JsonBean) {
                objectMap.put(key, ((JsonBean) value).toJSON());
            } else {
                objectMap.put(key, value);
            }
        }

        return objectMap;
    }

    @Override
    public String toJSONString() throws JsonFormatException {
        return toJSON().toString();
    }
}

package com.example.demo.common.collections;

import com.example.demo.beans.common.*;
import com.example.demo.common.monad.Function;
import com.example.demo.common.monad.Option;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * <pre>
 * DataSet&lt;Integer&gt; intSet = DataSet.of(1,2,3,4,5);
 * DataSet&lt;Integer&gt; oddSet = intSet.filter(new Function() {
 *     pubilc Boolean apply(Integer n) {
 *         return n % 2 == 0;
 *     }
 * });
 * </pre>
 * Created by Zav Deng/dengzf@asiainfo.com on 17-9-18.
 */
public class DataSet<E> extends AbstractDataSet<E> implements JsonArrayBean, Serializable {

    public enum Type {
        ARRAY,
        LINKED,
        QUEUE,
        STACK,
        HASHSET,
        TREESET
    }

    public DataSet() { super(); }

    public DataSet(int initCapacity) {
        super(initCapacity);
    }

    public DataSet(int initCapacity, Type type) {
        super(initCapacity, type);
    }

    public DataSet(List<E> dataList) {
        super(dataList);
    }

    public DataSet<E> sort() {
        return this.sort(null);
    }

    public static final <T> DataSet<T> of(List<T> elems) {
        return new DataSet<>(elems);
    }

    public static final <T extends JsonBean<?>> DataSet<T> fromJSON(JSONArray jsonArray, Class<T> typeClass)  throws JsonConvertException {
        if (jsonArray == null)
            throw new IllegalArgumentException("jsonArray参数不能为空");

        DataSet<T> beans= new DataSet<>();
        for (int i = 0 ; i < jsonArray.length(); i++) {
            if (!jsonArray.isNull(i)) {
                Object _value = jsonArray.get(i);
                if (!(_value instanceof JSONObject))
                    throw new JsonConvertException("CM102", "无法转换非JSONObject元素：" + i);
                beans.add(GenericBean.fromJSON((JSONObject)_value, typeClass));
            }
        }
        return beans;
    }

    public static final <T> DataSet<T> of(T ... elems) {
        DataSet<T> dataSet = new DataSet<>();
        for (T entry : elems) {
            dataSet.add(entry);
        }
        return dataSet;
    }

    public DataSet<E> sort(Comparator<E> comparator) {
        if (!(this.dataHolder instanceof List)) {
            throw new UnsupportedOperationException(type + " don't support sort operation");
        }
        Collections.sort((List<E>) this.dataHolder, comparator);
        return this;
    }

    public boolean disjoint(DataSet<E> s2) {
        return Collections.disjoint(this, s2);
    }

    public Option<E> safeGet(int index) {
        return Option.of(get(index));
    }

    public <T> DataSet<T> map(Function<E, T> f) {
        DataSet<T> dataSet = new DataSet<>();
        for (E entry : this) {
            dataSet.add(f.apply(entry));
        }
        return dataSet;
    }


    public DataSet<E> filter(Function<E, Boolean> f) {
        DataSet<E> dataSet = new DataSet<>();
        for (E entry : this) {
            if (f.apply(entry))
                dataSet.add(entry);
        }
        return dataSet;
    }

    @Override
    public JSONArray toJSON() throws JsonFormatException {
        JSONArray arrayObj = new JSONArray();
        for (E value : this) {
            if (value == null) {
                arrayObj.put(JSONObject.NULL);
            } else if (value instanceof JsonBean) {
                arrayObj.put(((JsonBean)value).toJSON());
            } else {
                arrayObj.put(value);
            }
        }
        return arrayObj;
    }

    @Override
    public String toJSONString() throws JsonFormatException {
        return toJSON().toString();
    }
}

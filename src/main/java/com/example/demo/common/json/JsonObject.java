package com.example.demo.common.json;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by alen on 17-10-16.
 */
public class JsonObject extends JsonValue<JSONObject> implements Mappable<JsonValue<JSONObject>>, Iterable<JsonObject.Entry> {

    public JsonObject(JSONObject jsonObject) {
        super(jsonObject);
    }

    public JsonObject(String jsonString) {
        this(new JSONObject(jsonString));
    }

    public Set<String> keySet() {
        return getValue().keySet();
    }

    public JsonValue get(String key) { return convert(getValue().get(key)); }

    @Override
    public boolean contains(String key) {
        return getValue().has(key);
    }

    @Override
    public Iterator<Entry> iterator() {
        final Iterator<String> keyIter = this.keySet().iterator();
        return new Iterator<Entry>() {
            @Override
            public boolean hasNext() {
                return keyIter.hasNext();
            }
            @Override
            public Entry next() {
                String key = keyIter.next();
                Object value = getValue().get(key);
                return new Entry(key, value);
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not implemented");
            }
        };
    }

    public class Entry {
        private String key;
        private Object object;

        public Entry(String key, Object object) {
            this.key = key;
            this.object = object;
        }

        public String getKey() {
            return key;
        }

        public Object getObject() {
            return object;
        }
    }
}

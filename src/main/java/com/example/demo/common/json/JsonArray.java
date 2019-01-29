package com.example.demo.common.json;

import org.json.JSONArray;

import java.util.Iterator;

/**
 * Created by alen on 17-10-16.
 */
public class JsonArray  extends JsonValue<JSONArray> implements Iterable<JsonValue<?>>, MappableList<JsonValue<?>> {

    public JsonArray(String jsonString) {
        this(new JSONArray(jsonString));
    }

    public JsonArray(JSONArray jsonArray) {
        super(jsonArray);
    }

    @Override
    public JsonValue<?> get(int index) {
        return convert(this.getValue().get(index));
    }

    public int length() {
        return getValue().length();
    }

    @Override
    public Iterator<JsonValue<?>> iterator() {
        return new JSONArrayIterator(this);
    }

    class JSONArrayIterator implements Iterator<JsonValue<?>> {
        int position;
        private JsonArray arrayObj;

        public JSONArrayIterator(JsonArray arrayObj) {
            this.arrayObj = arrayObj;
            this.position = 0;
        }

        @Override
        public boolean hasNext() {
            return this.position < this.arrayObj.length();
        }

        @Override
        public JsonValue next() {
            return this.arrayObj.get(this.position++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("immutable JSONArray doesn't support remove operation");
        }

    }

}

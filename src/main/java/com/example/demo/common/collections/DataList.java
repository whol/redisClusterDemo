package com.example.demo.common.collections;


import com.example.demo.common.monad.UnitFunction;

import static com.example.demo.common.collections.DataOp.cons;

/**
 * Created by alen on 17-9-21.
 */
public class DataList<T> {

    public static final DataList<?> NIL = new DataList<>(null, null);

    private final T head;

    private final DataList<T> rest;

    public static <T> DataList<T> empty() { return new DataList<T>(null, null); }

    public static <T> DataList<T> of(T ... item) {
        return make(item, 0);
    }

    private static <T> DataList<T> make(T[] entries, int index) {
        if (index >= entries.length)
            return DataList.empty();
        else {
            return cons(entries[index], make(entries, index + 1));
        }
    }

    public void forEach(UnitFunction<T> f) {
        iterate(this, f);
    }

    private void iterate(DataList<T> dataList, UnitFunction<T> f) {
        if (!dataList.isEmpty()) {
            f.apply(dataList.head());
            iterate(dataList.tail(), f);
        }
    }

    public DataList(T head, DataList<T> rest) {
        this.head = head;
        this.rest = rest;
    }

    public T head() { return this.head; }

    public DataList<T> tail() { return this.rest; }

    public boolean isEmpty() {
        return this.head == null && rest == null;
    }
}

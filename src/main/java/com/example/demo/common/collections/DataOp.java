package com.example.demo.common.collections;


import com.example.demo.common.monad.Function;

/**
 * Created by alen on 17-9-21.
 */
public class DataOp {

    public static final <T> DataList<T> cons(T head, DataList<T> tail) {
        return new DataList(head, tail == null ? DataList.empty() : tail);
    }

    public static final <A, B> DataList<B> map(DataList<A> dataList, Function<A, B> f) {
        if (dataList.isEmpty()) {
            return DataList.empty();
        } else {
            return cons(f.apply(dataList.head()), map(dataList.tail(), f));
        }
    }

}

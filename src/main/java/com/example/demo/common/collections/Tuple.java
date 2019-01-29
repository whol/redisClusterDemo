package com.example.demo.common.collections;

/**
 * Created by alen on 17-9-21.
 */
public class Tuple<T1, T2> {

    final T1 first;

    final T2 second;

    public static final <T1, T2> Tuple<T1,T2> of(T1 v1, T2 v2) {
        return new Tuple<>(v1, v2);
    }

    Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    public String toString() {
        return String.format("<#Pair ('%s', '%s')>", this.getFirst(), this.getSecond());
    }
}

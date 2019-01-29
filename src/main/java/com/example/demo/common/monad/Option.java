package com.example.demo.common.monad;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-9-18.
 */
public class Option<E> {

    protected E object;

    public static <T> Option<T> of(T object) {
        if (object == null) {
            return new Option<>(object);
        } else {
            Class<?> type = object.getClass();
            if (type == String.class)
                return (Option<T>)new StringOption((String) object);
            else
                return new Option<>(object);
        }
    }

    public Option(E object) {
        this.object = object;
    }

    public boolean isNothing() {
        return this.object == null;
    }

    public E get() {
        return object;
    }

    public <R> Option<R> map(Function<E, R> fn) {
        return Option.of(fn.apply(this.object));
    }

}


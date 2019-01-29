package com.example.demo.common.monad;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-9-18.
 */
public interface Function<T, R> {

    R apply(T input);

}

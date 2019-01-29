package com.example.demo.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Created by alen on 17-10-28.
 */
public abstract class TrackableValueFormatter implements ValueFormatter {

    private Object handler;

    private Environment environment;

    private ApplicationContext applicationContext;

    public Object getHandler() {
        return handler;
    }

    public void setHandler(Object handler) {
        this.handler = handler;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}

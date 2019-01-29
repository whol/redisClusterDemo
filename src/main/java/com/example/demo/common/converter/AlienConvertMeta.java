package com.example.demo.common.converter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-8-9.
 */
public class AlienConvertMeta {

    private final Class<? extends AlienConverter> converterClass;

    private final Set<String> alienClassSet = new HashSet<>();

    public AlienConvertMeta(Class<? extends AlienConverter> converterClass, String[] alienClasses) {
        this.converterClass = converterClass;
        for (String alienClass : alienClasses) {
            alienClassSet.add(alienClass);
        }
    }

    public boolean containsClass(String className) {
        return alienClassSet.contains(className);
    }

    public Class<? extends AlienConverter> getConverterClass() {
        return converterClass;
    }
}

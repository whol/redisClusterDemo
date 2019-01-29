package com.example.demo.common.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-8-9.
 */
public class AlienObjectConvertService {

    private final AlienConvertMeta[] alienConvertMetas;

    private Map<String, AlienConverter> alienConverterMap = new HashMap<String, AlienConverter>();

    public AlienObjectConvertService(AlienConvertMeta[] alienConvertMetas) {
        this.alienConvertMetas = alienConvertMetas;
        for (AlienConvertMeta meta : alienConvertMetas) {
            String converterClassName = meta.getConverterClass().getName();
            if (!alienConverterMap.containsKey(converterClassName)) {
                try {
                    alienConverterMap.put(converterClassName, meta.getConverterClass().newInstance());
                } catch (Throwable ex) {
                    throw new IllegalArgumentException("无法创建实例：" + converterClassName, ex);
                }
            }
        }
    }

    private AlienConverter determineConverter(String className) {
        for (AlienConvertMeta meta : alienConvertMetas) {
            if (meta.containsClass(className))
                return alienConverterMap.get(meta.getConverterClass().getName());
        }
        return null;
    }

    public boolean isConvertable(Object value) {
        return value != null && determineConverter(value.getClass().getName()) != null;
    }

    public Object convert(Object value) throws ConvertException {
        if (value != null) {
            String className = value.getClass().getName();
            AlienConverter converter = determineConverter(className);
            if (converter != null) {
                return converter.convert(value);
            }
        }
        return value;
    }
}

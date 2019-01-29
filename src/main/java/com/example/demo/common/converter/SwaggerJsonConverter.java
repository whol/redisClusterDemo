package com.example.demo.common.converter;

import org.apache.commons.beanutils.MethodUtils;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-8-9.
 */
public class SwaggerJsonConverter extends AbstractAlienConverter {

    @Override
    public Object convert(Object alienObj) throws ConvertException {
        if (alienObj != null) {
            try {
                return MethodUtils.invokeExactMethod(alienObj, "value", null);
            } catch (Exception e) {
                throw new ConvertException("Not a swagger json object", e);
            }
        }
        return null;
    }
}

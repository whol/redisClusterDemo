package com.example.demo.common.http;

import com.example.demo.beans.common.MultipartBean;
import com.example.demo.common.domain.ServiceInput;
import com.example.demo.common.json.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 统一入参转换处理类
 * Created by Zav Deng/dengzf@asiainfo.com on 17-10-11.
 */
public class ServiceInputArgumentResolver implements org.springframework.web.method.support.HandlerMethodArgumentResolver {

    private ObjectMapper objectMapper;

    public ServiceInputArgumentResolver() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ServiceInput.class == parameter.getParameterType()
                || MultipartBean.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        if (webRequest instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
            if (parameter.getParameterType() == ServiceInput.class) {
                return new ServiceInput(servletWebRequest.getRequest());
            } else if (MultipartBean.class.isAssignableFrom(parameter.getParameterType())) {
                return objectMapper.readObject(new ServiceInput(servletWebRequest.getRequest()), parameter.getParameterType());
            } else {
                throw new UnsupportedOperationException("Unsupported type: " + parameter.getParameterType());
            }
        } else {
            throw new IllegalStateException("Expecting ServletWebRequest, but " + webRequest);
        }
    }
}

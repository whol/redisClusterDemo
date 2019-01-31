package com.example.demo.intercepter;

import com.example.demo.beans.common.UserInfo;
import com.example.demo.common.Constants;
import com.example.demo.utils.annotation.UserType;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 用户是否登陆状态的注解数据获取
 */
public class UserTypeArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(UserInfo.class)
                && methodParameter.hasParameterAnnotation(UserType.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return (UserInfo) nativeWebRequest.getAttribute(Constants.USER_TYPE_SESSION,
                RequestAttributes.SCOPE_REQUEST);
    }
}

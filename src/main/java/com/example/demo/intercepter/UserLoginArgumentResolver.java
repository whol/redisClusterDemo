package com.example.demo.intercepter;

import com.example.demo.common.Constants;
import com.example.demo.entity.FantasyUser;
import com.example.demo.utils.annotation.LoginUser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 登陆信息的注解数据获取
 */
public class UserLoginArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(FantasyUser.class)
                && methodParameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        FantasyUser user = (FantasyUser) nativeWebRequest.getAttribute(Constants.USER_TYPE_SESSION,
                RequestAttributes.SCOPE_REQUEST);
        if (null != user) {
            return user;
        }
        throw new Exception("4");
    }
}

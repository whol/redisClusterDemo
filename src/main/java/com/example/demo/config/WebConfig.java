package com.example.demo.config;

import com.example.demo.intercepter.AuthorizationInterceptor;
import com.example.demo.intercepter.UserLoginArgumentResolver;
import com.example.demo.intercepter.UserTypeArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.example.demo", "com.example.demo.controller.**", "com.example.demo.intercepter.**"},
excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class)})
public class WebConfig extends DefaultWebMvcConfig {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**")
                .excludePathPatterns("/system/login/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/va/**")
                .excludePathPatterns("/error/**")
                .excludePathPatterns("/net/busi/busiIntf/**")
                .excludePathPatterns("/net/group/id/**")
                .excludePathPatterns("/user/userLike/collection/**")
                .excludePathPatterns("/recommend/in/**");
        //registry.addInterceptor(visitResInterceptor()).addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserArgumentResolver());
        argumentResolvers.add(userTypeArgumentResolver());
        super.addArgumentResolvers(argumentResolvers);
    }

    /**
     * 登陆用户能通过这个注解获取到登陆用户信息
     * @return
     */
    @Bean
    public UserLoginArgumentResolver currentUserArgumentResolver() {
        return new UserLoginArgumentResolver();
    }

    /**
     * 拦截器加载的时间点在springcontext之前，拦截器里的Reference，肯定是会为空的
     * 注成Bean，在拦截器加载的时候再注入
     * @return
     */
    @Bean
    public AuthorizationInterceptor authorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    /**
     * 用户登陆状态校验，是游客还是管理员等
     * @return
     */
    @Bean
    public UserTypeArgumentResolver userTypeArgumentResolver() {
        return new UserTypeArgumentResolver();
    }

}

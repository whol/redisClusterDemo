package com.example.demo.common.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/***
 *
 * @author zhuanghd
 * @author Zav Deng/dengzf@asiainfo.com
 * */
@Slf4j
public class MethodReturnValueHandlerFactoryBean implements ApplicationContextAware, InitializingBean {

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        adapter.setReturnValueHandlers(decorateHandlers(new ArrayList<>(adapter.getReturnValueHandlers())));
        log.info("完成统一消息封装对象初始化");
    }

    private List<HandlerMethodReturnValueHandler> decorateHandlers(List<HandlerMethodReturnValueHandler> handlers)
    {
        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (handler instanceof RequestResponseBodyMethodProcessor) {
                //用自己的ResponseBody包装类替换掉框架的，达到返回Result的效果
                MethodReturnValueHandlerDelegate decorator = new MethodReturnValueHandlerDelegate(applicationContext, handler);
                int index = handlers.indexOf(handler);
                handlers.set(index, decorator);
                break;
            }
        }
        return handlers;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

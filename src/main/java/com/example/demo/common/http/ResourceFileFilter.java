package com.example.demo.common.http;

//import com.cmos.common.web.upload.config.StorageConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <pre>
 * 静态资源过滤器，负责处理客户端的静态资源请求
 * 默认使用 @ResourceHttpRequestHandler处理，但由于前端框架组件的请求默认使用POST方法请求JSON文件
 * 所以，创建该过滤兼容前端组件
 * </pre>
 * Created by Zav Deng/dengzf@asiainfo.com on 17-2-9.
 */
public class ResourceFileFilter extends ResourceLocator implements Filter, ApplicationContextAware {

    private static final Logger LOGGER = Logger.getLogger(ResourceFileFilter.class);

    private ApplicationContext applicationContext;

    public ResourceFileFilter() {
        super();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String uri = ((HttpServletRequest)request).getRequestURI();

        if (uri.indexOf("/~files") != -1) {
            chain.doFilter(request, response);
        } else {
            if (uri != null && uri.endsWith("/")) {
                chain.doFilter(request, response);
            } else {
                if (!writeRequestResource((HttpServletRequest) request, (HttpServletResponse) response)) {
                    chain.doFilter(request, response);
                }
            }
        }
    }

    @Override
    public void destroy() {

    }


    @Override
    public ResourceLoader getResourceLoader() {
        return this.applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

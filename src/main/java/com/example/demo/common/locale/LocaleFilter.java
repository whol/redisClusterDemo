package com.example.demo.common.locale;

import com.alibaba.dubbo.rpc.RpcContext;
import com.example.demo.common.utils.LocaleMessageSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by LIUYAN on 2018/3/8.
 */
@Slf4j
public class LocaleFilter implements Filter {
    private static Locale defaultLanaguage = new Locale("zh", "CN");
    private ApplicationContext applicationContext;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        //#########国际化支持#########
        String[] lang_country = null;
        String  defalut_country="zh_CN";
        LocaleMessageSourceUtil.locLanguage.set(defaultLanaguage);
        //先从请求头中读取扩展头x-expect-language对应的语言
        String lang_param = httpServletRequest.getHeader(LocaleMessageSourceUtil.REQUEST_HEADER_LANG_CONSTANT);
        if(StringUtils.isNotEmpty(lang_param)) {
            lang_country  = lang_param.split("_");
        } else {
            RpcContext.getContext().setAttachment("locale",defalut_country);
        }
        if(lang_country!=null && lang_country.length>=2) {
            Locale locLan=new Locale(lang_country[0],lang_country[1]);
            LocaleMessageSourceUtil.locLanguage.set(locLan);
            RpcContext.getContext().setAttachment("locale", lang_param);
        }
        chain.doFilter(request, response);
    }
    @Override
    public void destroy() {

    }
}

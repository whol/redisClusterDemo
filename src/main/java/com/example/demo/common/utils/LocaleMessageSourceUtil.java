package com.example.demo.common.utils;

import com.alibaba.dubbo.rpc.RpcContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by Lenovo on 2018/2/26.
 */
public class LocaleMessageSourceUtil implements ApplicationContextAware {
    private static MessageSource messageSource;
    private static ApplicationContext applicationContext;
    /**
     * 输入对象中的语言参数(x-expect-language)
     */
    public static final String REQUEST_HEADER_LANG_CONSTANT = "x-expect-language";
    /**
     * 本地语言-线程变量
     */
    public static ThreadLocal<Locale> locLanguage = new ThreadLocal<Locale>() {
        public Locale initialValue() {
            // 初始化为简体中文
            return Locale.SIMPLIFIED_CHINESE;
        }
    };

    public void setMessageSource(MessageSource messageSource) {
        LocaleMessageSourceUtil.messageSource = messageSource;
    }

    public static String getMessage(String code) {
        return getMessage(code, null);
    }

    /**
     * @param code ：对应messages配置的key.
     * @param args : 数组参数.
     * @return
     */
    public static String getMessage(String code, Object[] args) {
        return getMessage(code, args, "");
    }

    /**
     * @param code           ：对应messages配置的key.
     * @param args           : 数组参数.
     * @param defaultMessage : 没有设置key的时候的默认值.
     * @return
     */
    public static String getMessage(String code, Object[] args, String defaultMessage) {
    	
    	if(RpcContext.getContext().isProviderSide()) {
            String I18N_PARAM = "locale";
            String i18nParamVal = RpcContext.getContext().getAttachment(I18N_PARAM);
    		if (i18nParamVal != null) {
    			String[] lang_country  = i18nParamVal.split("_");
    			LocaleMessageSourceUtil.locLanguage.set(new Locale(lang_country[0],lang_country[1]));
    		}
    	}
        //这里使用比较方便的方法，不依赖request.
        Locale locale = LocaleMessageSourceUtil.locLanguage.get();
		
        String value = null;
        try {
            value = messageSource.getMessage(code, args, defaultMessage, locale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * @param code           ：对应messages配置的key.
     * @param args           : 数组参数.
     * @param defaultMessage : 没有设置key的时候的默认值.
     * @param request
     * @return
     */
    public String getMessage(String code, Object[] args, String defaultMessage, HttpServletRequest request) {
        Locale locale = RequestContextUtils.getLocale(request);
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static MessageSource getMessageSource() {
        return messageSource;
    }


    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}

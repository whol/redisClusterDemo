package com.example.demo.config;

import com.example.demo.common.exception.autoconfig.EnableGeneralException;
import com.example.demo.common.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ObjectToStringHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.List;

/**
 *  默认WEB容器配置
 *
 * Created by Zav Deng/dengzf@asiainfo.com on 17-1-12.
 */
@Configuration
@EnableGeneralException
@ComponentScan(basePackages = {"com.example.demo"})
@EnableWebMvc
public class DefaultWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private Environment environment;

    @Bean
    public FilterRegistrationBean characterEncodingFilterRegistrationBean() {
        FilterRegistrationBean filterBean = new FilterRegistrationBean();
        filterBean.setFilter(new CharacterEncodingFilter(getCharsetName(), true, true));
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }

    private String getCharsetName() {
        String charsetName = environment.getProperty(Constants.K_DEFAULT_CHARSET);
        if  (StringUtils.isEmpty(charsetName)) {
            charsetName = Constants.DEFAULT_CHARSET;
        }
        return charsetName;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter(Charset.forName(getCharsetName())));
        converters.add(new ObjectToStringHttpMessageConverter(new DefaultFormattingConversionService(), Charset.forName(getCharsetName())));
        converters.add(new ResourceHttpMessageConverter());
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

}

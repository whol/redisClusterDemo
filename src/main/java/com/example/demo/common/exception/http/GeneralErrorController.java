package com.example.demo.common.exception.http;

import com.cmos.common.exception.GeneralException;
import com.cmos.common.exception.KeepStatusExeption;
import com.example.demo.common.http.ResourceLocator;
import com.example.demo.common.utils.Constants;
import com.example.demo.common.utils.JSONUtils;
import com.example.demo.common.utils.LocaleMessageSourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ObjectToStringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 统一异常全局控制器
 * <p>处理404/500等服务器端错误,返回符合RESTFul规范的JSON数据</p>
 *
 * @author Zifeng.d 2016-12-02 新增统一异常处理的错误编码
 * @author zhuanghd 根据RESTful API规范实现统一异常消息封装
 * @since 0.0.1
 */
//@Controller()
public class GeneralErrorController extends AbstractErrorController implements ApplicationContextAware {

    private static final String DEFAULT_WELCOME_PAGE = "index.html";

    private static GeneralErrorController generalErrorController;

    private static final Logger LOGGER = Logger.getLogger(GeneralErrorController.class);

    /**
     * Error Attributes in the Application
     */
    @Autowired
    private ErrorAttributes errorAttributes;

    /**
     * Spring global environment
     * */
    @Autowired
    private Environment environment;

    private final static String ERROR_PATH = "/error";

    private static final String DEFAULT_OUTPUT_CHARSET = "UTF-8";

    private ErrorProperties errorProperties;

    private ApplicationContext applicationContext;

    private ResourceLocator resourceLocator;

    private final static String LOGGER_EXCEPTION_SWITCH = "cmos.logger.errorlog.enable";

    public GeneralErrorController() {
        this(new DefaultErrorAttributes(), new ErrorProperties(), Collections.<ErrorViewResolver>emptyList());
    }

    /**
     * Create a new {@link GeneralErrorController} instance.
     * @param errorAttributes the error attributes
     * @param errorProperties configuration properties
     */
    public GeneralErrorController(ErrorAttributes errorAttributes,
                                ErrorProperties errorProperties) {
        this(errorAttributes, errorProperties,
                Collections.<ErrorViewResolver>emptyList());
    }

    /**
     * Create a new {@link GeneralErrorController} instance.
     * @param errorAttributes the error attributes
     * @param errorProperties configuration properties
     * @param errorViewResolvers error view resolvers
     */
    public GeneralErrorController(ErrorAttributes errorAttributes,
                                ErrorProperties errorProperties, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
        Assert.notNull(errorProperties, "ErrorProperties must not be null");
        this.errorProperties = errorProperties;
        this.resourceLocator = new ResourceLocator() {
            @Override
            public ResourceLoader getResourceLoader() {
                return applicationContext;
            }
        };
    }

    //@RequestMapping(path = ERROR_PATH, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    //@ResponseBody
    public ResponseEntity<Map<String, Object>> errorJson(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Map<String, Object>>(body, status);
    }

    /**
     * Supports other formats like JSON, XML
     * @param request
     * @return
     *
     *
     *
     */
    @RequestMapping(path = ERROR_PATH, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public void error(HttpServletRequest request,
                      HttpServletResponse response) throws IOException, ServletException
    {
        errorInternal(new ErrorServletRequestFacade(MediaType.APPLICATION_JSON_UTF8_VALUE, request), response);
    }

    private void errorInternal(HttpServletRequest request,
                               HttpServletResponse response) throws IOException, ServletException
    {
        Map<String, Object> result = new HashMap<String, Object>();

        WebRequest requestAttributes = null;

        String requestURL = request.getRequestURI();
        HttpStatus status = getStatus(request);

        // 默认使用HTTP状态码作为结果状态码
        String errorCode = String.valueOf(status.value());
        String errorMessage = null;
        String internationMessage=null;
        boolean keepStatusCode = status == HttpStatus.NOT_FOUND;

        if(status == HttpStatus.NOT_FOUND) {
            if (requestURL != null && requestURL.endsWith("/")) {
                // 请求路径不存在，尝试访问欢迎页面
                String welcomePage = requestURL + getWelcomePage();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("尝试访问欢迎页面：" + welcomePage);
                }

                if (resourceLocator.getResource(request, welcomePage) != null) {
                    // 重定向至欢迎页面，刷新HTTP响应状态码
                    response.sendRedirect(welcomePage);
                } else {
                    response.sendError(404);
                }
                return;
            }
        } else {
            requestAttributes = new ServletWebRequest(request);
            Throwable error = errorAttributes.getError(requestAttributes);
            if (error != null) {
                if (error instanceof GeneralException) {
                    // 使用异常错误状态码
                    errorCode = ((GeneralException)error).getErrorCode();
                }
                result.put("exception", error.getClass());
                LOGGER.error(error.getMessage(), error);
                keepStatusCode = error instanceof KeepStatusExeption;
            }
        }

        // 使用配置文件的错误码与错误描述映射配置
        errorMessage = environment.getProperty(errorCode);
        internationMessage = LocaleMessageSourceUtil.getMessage(errorCode, null, "");
        if (StringUtils.isNotBlank(internationMessage)) {
            errorMessage = internationMessage;
        }
        if (StringUtils.isBlank(errorMessage)) {
            if (requestAttributes != null) {
                Map<String, Object> errorAttributes = getErrorAttributes(requestAttributes, requestURL, true);
                errorMessage = (String) errorAttributes.get("message");
            }
        }

        // 直接使用当前HTTP请求异常描述
        if(StringUtils.isBlank(errorMessage)) {
            errorMessage = status.getReasonPhrase();
        }

        boolean normalError = Boolean.valueOf(environment.getProperty(Constants.K_NORMAL_STATUS, "true"));
        if (normalError && !keepStatusCode) {
            // 正常返回请求，HTTP应答状态码设置为 200 成功标识
            response.setStatus(HttpStatus.OK.value());
        }

        //打印异常信息给日志平台
        if (environment.getProperty(LOGGER_EXCEPTION_SWITCH, Boolean.class, false)){
            Throwable error = errorAttributes.getError(requestAttributes);
            CmosLoggerAdapter.recordException(errorMessage, error);
        }
        MyErrorResult errorResult = new MyErrorResult(errorCode, errorMessage, requestURL, new ServletException(errorMessage));
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();

        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
        ObjectToStringHttpMessageConverter messageConverter = new ObjectToStringHttpMessageConverter(conversionService, Charset.forName(DEFAULT_OUTPUT_CHARSET));

        messageConverter.write(JSONUtils.toJSON(errorResult), MediaType.APPLICATION_JSON, outputMessage);
    }

    private String getWelcomePage() {
        return environment.getProperty("spring.webmvc.welcome-page", DEFAULT_WELCOME_PAGE);
    }

    /**
     * Returns the path of the error page.
     *
     * @return the error path
     */
    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * 获取错误消息
     * @param requestAttributes
     * @param url
     * @param includeStackTrace
     * @return
     */
    private Map<String, Object> getErrorAttributes(WebRequest requestAttributes,
                                                   String url,
                                                   boolean includeStackTrace) {
        Map<String, Object> map = this.errorAttributes.getErrorAttributes(requestAttributes,includeStackTrace);
        map.put("URL", url);
        return map;
    }

    /**
     * 获取Http状态
     * @param request
     * @return
     */
    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            }
            catch (Exception ex) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * Determine if the stacktrace attribute should be included.
     * @param request the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the stacktrace attribute should be included
     */
    protected boolean isIncludeStackTrace(HttpServletRequest request,
                                          MediaType produces) {
        ErrorProperties.IncludeStacktrace include = getErrorProperties().getIncludeStacktrace();
        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
            return getTraceParameter(request);
        }
        return false;
    }

    /**
     * Provide access to the error properties.
     * @return the error properties
     */
    protected ErrorProperties getErrorProperties() {
        return this.errorProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

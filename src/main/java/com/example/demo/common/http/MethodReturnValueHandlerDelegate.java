package com.example.demo.common.http;

import com.example.demo.common.annotation.CompatibleOutput;
import com.example.demo.common.domain.UserResult;
import com.example.demo.common.utils.JSONUtils;
import com.example.demo.common.utils.ResultFormatter;
import com.example.demo.common.utils.TrackableValueFormatter;
import com.example.demo.common.utils.ValueFormatter;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

/**
 * ResponseBody包装类，返回Result
 */
public class MethodReturnValueHandlerDelegate implements HandlerMethodReturnValueHandler {

    private static final Logger LOGGER = Logger.getLogger(MethodReturnValueHandlerDelegate.class);

    private final ApplicationContext applicationContext;

    private static class ParameterHelper {

        MethodParameter parameter;

        public ParameterHelper(MethodParameter parameter) {
            this.parameter = parameter;
        }

        Method getMethod() {
            return parameter != null ? parameter.getMethod() : null;
        }

        Class<?> getDeclaringClass() {
            return parameter != null ? parameter.getDeclaringClass() : null;
        }

        <T extends Annotation> T getClassAnnotation(Class<T> annotationClass) {
            return parameter != null ? parameter.getDeclaringClass().getAnnotation(annotationClass) : null;
        }

        <T extends Annotation> boolean hasClassAnnotation(Class<T> annotationClass) {
            return parameter != null && getClassAnnotation(annotationClass) != null;
        }

        <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationClass) {
            return getMethodAnnotation(annotationClass) != null;
        }

        <T extends Annotation> T getMethodAnnotation(Class<T> annotationClass) {
            return parameter != null ? getMethod().getAnnotation(annotationClass) : null;
        }

        public boolean isKeepOrigin() {
            return hasClassAnnotation(CompatibleOutput.class);
        }

        public boolean isRestController() {
            return hasClassAnnotation(RestController.class);
        }

        public ResultFormatter getResultFormatter() {
            return getMethodAnnotation(ResultFormatter.class);
        }

        public boolean isCompatibleOutput() {
            return hasMethodAnnotation(CompatibleOutput.class);
        }

        public ValueFormatter createValueFormater(Class<? extends ValueFormatter> aClass) throws IllegalAccessException, InstantiationException {
            if (aClass != null) {
                return aClass.newInstance();
            }
            return null;
        }

    }

    private final HandlerMethodReturnValueHandler targetHandler;

    public MethodReturnValueHandlerDelegate(ApplicationContext applicationContext, HandlerMethodReturnValueHandler targetHandler) {
        this.applicationContext = applicationContext;
        this.targetHandler = targetHandler;
    }

    @Override
    public boolean supportsReturnType(MethodParameter parameter) {
        return targetHandler.supportsReturnType(parameter);
    }



    @Override
    public void handleReturnValue(Object returnValue, MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        ParameterHelper helper = new ParameterHelper(parameter);

        boolean isErrorResult = returnValue instanceof ErrorResult;
        // 是否为异常返回结果
        ErrorResult errorResult = isErrorResult ?  (ErrorResult)returnValue : null;
        // 是否为REST控制器
        boolean renderRestResult = helper.isRestController() || isErrorResult;
        // 是否保持原始返回结果，方法级优级最高
        boolean keepOrigin = helper.isCompatibleOutput();

        ValueFormatter valueFormatter = null;

        ResultFormatter formatter = helper.getResultFormatter();
        if (formatter != null) {
            valueFormatter = helper.createValueFormater(formatter.formatterClass());
            if (valueFormatter != null && valueFormatter instanceof TrackableValueFormatter) {
                TrackableValueFormatter trackableValueFormatter = (TrackableValueFormatter) valueFormatter;
                trackableValueFormatter.setHandler(parameter.getMethod());
                trackableValueFormatter.setApplicationContext(applicationContext);
                trackableValueFormatter.setEnvironment(applicationContext.getEnvironment());
            }
        }

        if (!keepOrigin) {
            // 类级原始返回结果
            keepOrigin = helper.hasClassAnnotation(CompatibleOutput.class);
        }

        if (keepOrigin) {
            returnValue = returnValue != null ? JSONUtils.toJSON(returnValue, valueFormatter) : new JSONObject();
            targetHandler.handleReturnValue(returnValue, parameter, mavContainer, webRequest);
            return;
        }

        // 如果是REST控制器,需要对结果进行包装
        if (renderRestResult) {
            Object data = returnValue == null ? Collections.EMPTY_MAP : returnValue;
            // 如果返回值不为ResponseEntity,则进行包装
            if (returnValue instanceof ResponseEntity) {
                data = ((ResponseEntity) returnValue).getBody();
            }

            // TODO: 后续对下面的代码进行整理优化
            // 将返回对象序列化为JSON对象
            if (isErrorResult || data instanceof UserResult) {
                returnValue = JSONUtils.toJSON(data, valueFormatter);
            } else {
                JSONObject jsonResult = new JSONObject();
                if (data instanceof Collection) {
                    JSONObject bean = new JSONObject();
                    bean.put("total", ((Collection) data).size());

                    jsonResult.put("bean", bean);
                    jsonResult.put("beans", JSONUtils.toJSON(data, valueFormatter));
                } else if (data instanceof PageInfo) {
                    JSONObject bean = new JSONObject();

                    PageInfo pageInfo = (PageInfo) data;
                    bean.put("total", pageInfo.getTotal());
                    bean.put("pageNum", pageInfo.getPageNum());
                    bean.put("pageSize", pageInfo.getPageSize());
                    bean.put("pageCount", pageInfo.getPages());

                    jsonResult.put("bean", bean);
                    jsonResult.put("beans", JSONUtils.toJSON(pageInfo.getList(), valueFormatter));
                } else {
                    jsonResult.put("bean", JSONUtils.toJSON(data, valueFormatter));
                    jsonResult.put("beans", new JSONArray());
                }
                jsonResult.put("object", new JSONObject());
                jsonResult.put("returnMessage", "");
                jsonResult.put("returnCode", 0);
                returnValue = jsonResult;
            }
        }
        if (!isErrorResult) {
            targetHandler.handleReturnValue(returnValue, parameter, mavContainer, webRequest);
        }
    }

}

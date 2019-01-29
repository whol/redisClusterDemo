package com.example.demo.cache.ttl.aspect;

import com.example.demo.cache.ttl.annotation.CacheTTL;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.Order;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Spring cache 缓存生存时间注解切面
 *
 * @author zhuanghd
 * @since 1.0
 */
@Aspect
@Order(0)
@Slf4j
public class AnnotationCacheTTLAspect {

    @Around(value = "@annotation(com.example.demo.cache.ttl.annotation.CacheTTL)")
    public Object aroundAspect(ProceedingJoinPoint pj) throws Throwable {
        Method method = getTargetMethod(pj);
        if (isNeedTTL(pj)) {
            log.debug("需要进行缓存生命周期设置,方法[" + method + "]");
            return executeAspect(pj);
        } else {
            log.warn("缓存生命时间设置已忽略,方法[" + method + "]");
            return pj.proceed();
        }
    }

    /**
     * 判断是否需要生存时间设置
     * 如果包含Cacheable/CachePut注解,则需要进行生存时间设置,否则不设置
     *
     * @return
     */
    private boolean isNeedTTL(ProceedingJoinPoint pj) throws NoSuchMethodException {
        Class<?> classTarget = pj.getTarget().getClass();
        String methodName = pj.getSignature().getName();
        Class<?>[] par = ((MethodSignature) pj.getSignature()).getParameterTypes();
        Method objMethod = classTarget.getMethod(methodName, par);
        Annotation[] annotations = objMethod.getAnnotations();
        for (Annotation ann : annotations) {
            if (ann instanceof Cacheable
                    || ann instanceof CachePut) {
                System.out.println("找到注解");
                return true;
            }
        }
        log.warn("CacheTTL注解需配合'Cacheable或CachePut'使用,方法[" + objMethod + "]");
        return false;
    }

    /**
     * 执行缓存路由切面
     *
     * @return
     * @throws Throwable
     */
    private Object executeAspect(ProceedingJoinPoint pj) throws Throwable {
        Class<?> classTarget = pj.getTarget().getClass();
        String methodName = pj.getSignature().getName();
        Class<?>[] par = ((MethodSignature) pj.getSignature()).getParameterTypes();
        Method objMethod = classTarget.getMethod(methodName, par);
        CacheTTL annotation = objMethod.getAnnotation(CacheTTL.class);
        Object result = null;
        try {
            // 设置线程变量并执行后续缓存操作
            TTLHolder.setTTL(annotation);
            result = pj.proceed();
        } finally {
            // 确保清除线程变量
            TTLHolder.setTTL(null);
        }
        return result;
    }

    /**
     * 获取目标方法
     *
     * @param pj
     * @return
     */
    private Method getTargetMethod(ProceedingJoinPoint pj) {
        MethodSignature signature = (MethodSignature) pj.getSignature();
        return signature.getMethod();
    }

}

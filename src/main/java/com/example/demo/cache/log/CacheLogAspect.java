package com.example.demo.cache.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Order(4)
@Component
@Aspect
/**
 * 服务APi统一异常日志处理
 *
 * @author liuyan
 * @since 1.0
 */
public class CacheLogAspect {
    /**
     * 切点：针对RedisCacheServiceImpl中的方法
     */
    @Pointcut("execution(* com.example.demo.service.impl.*.*(..))")
    private void doLog() {
    }

    /**
     * 切点：针对有MultiKeys注解的方法
     */
    @Pointcut("@annotation(com.example.demo.cache.log.MultiKeys)")
    private void doMultiKeysLog() {
    }

    /**
     * 切点：针对config、ha、redis、route、PropertiesUtil中的方法和有NoKey注解的方法
     */
    @Pointcut("execution(* com.example.demo.config.*.*(..)) || execution(* com.cmos.cache.ha.*.*(..)) || execution(* com.cmos.cache.redis.*.*(..)) || execution(* com.cmos.cache.route.*.*(..)) || @annotation(com.example.demo.cache.log.NoKey)")
    private void doNoKeyLog() {
    }

    /**
     * 针对RedisCacheServiceImpl中的方法，且该方法没有MultiKeys和NoKey注解
     *
     * @param joinPoint
     * @param e
     * @throws Throwable
     */
    @AfterThrowing(value = "doLog()", throwing = "e")
    public void logAfterThrowingException(JoinPoint joinPoint, Exception e) throws Throwable {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Annotation[] annotations = method.getAnnotations();
        for (Annotation ann : annotations) {
            if (ann instanceof MultiKeys
                    || ann instanceof NoKey) {
                return;
            }
        }
        writeLog(joinPoint, e, joinPoint.getArgs()[0].toString());
    }

    /**
     * 针对有MultiKeys注解的方法
     *
     * @param joinPoint
     * @param e
     * @throws Throwable
     */
    @AfterThrowing(value = "doMultiKeysLog()", throwing = "e")
    public void logAfterThrowingExceptionMultiKeys(JoinPoint joinPoint, Exception e) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String key = "";
        for (Object arg :
                args) {
            key += (key.length() > 0 ? "," : "") + arg.toString();
        }
        writeLog(joinPoint, e, key);
    }

    /**
     * 针对config、ha、redis、route、PropertiesUtil中的方法和有NoKey注解的方法
     *
     * @param joinPoint
     * @param e
     * @throws Throwable
     */
    @AfterThrowing(value = "doNoKeyLog()", throwing = "e")
    public void logAfterThrowingConfigException(JoinPoint joinPoint, Exception e) throws Throwable {
        writeLog(joinPoint, e, "");
    }

    /**
     * 写入日志平台
     *
     * @param joinPoint
     * @param e
     * @param key
     */
    private void writeLog(JoinPoint joinPoint, Exception e, String key) {
        LogUtil.writeLog(getFullClassName(joinPoint) + "." + getMethodName(joinPoint), e, key, joinPoint.getTarget().getClass());
    }

    /**
     * 获取方法名
     *
     * @param joinPoint
     * @return
     */
    private String getMethodName(JoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getName();
    }

    private String getFullClassName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getName();
    }

}
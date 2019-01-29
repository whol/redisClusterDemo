package com.example.demo.cache.route.aspect;

import com.example.demo.cache.cacheException.CMOSCacheBaseException;
import com.example.demo.cache.cacheException.CachePropertyException;
import com.example.demo.cache.cacheException.RouteException;
import com.example.demo.cache.config.RouteConfig;
import com.example.demo.cache.redis.JedisClusterFactory;
import com.example.demo.cache.route.ICacheRouter;
import com.example.demo.cache.route.annotation.CacheRoute;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import redis.clients.jedis.JedisCluster;

import java.lang.reflect.Method;

/**
 * Spring cache 缓存路由注解切面
 * <p>用于解析路由注解中的参数,并设置和清除JedisCluster线程变量</p>
 *
 * @author zhuanghd
 * @since 1.0
 */
@Aspect
@Order(0)
public class AnnotationCacheRouteAspect {

    private ExpressionEvaluator evaluator = new ExpressionEvaluator();

    @Around(value = "@annotation(com.example.demo.cache.route.annotation.CacheRoute)")
    public Object aroundAspect(ProceedingJoinPoint pj) throws Throwable {
        if (!RouteConfig.usingRoute()) {
            // 如果是单集群,直接进行缓存操作就可以了,无需执行路由切面
            return pj.proceed();
        } else {
            // 如果是集群路由, 需执行路由切面
            return executeRouteAspect(pj);
        }
    }

    /**
     * 执行缓存路由切面
     * @param pj
     * @return
     * @throws Throwable
     */
    private Object executeRouteAspect(ProceedingJoinPoint pj) throws Throwable {
        MethodSignature signature = (MethodSignature) pj.getSignature();
        Method method = signature.getMethod();
        Object target = pj.getTarget();
        Class targetClass = getTargetClass(target);
        // 缓存路由
        JedisCluster cluster = findJedisCluster(pj, method, targetClass, target);
        Object obj = null;
        try {
            // 设置线程变量并执行后续缓存操作
            ClusterHolder.setCluster(cluster);
            obj = pj.proceed();
        } finally {
            // 确保清除线程变量
            ClusterHolder.setCluster(null);
        }
        return obj;
    }

    /**
     * 寻找JedisCluster
     * 根据注解上的参数和路由规则寻找集群
     * @return
     */
    private JedisCluster findJedisCluster(ProceedingJoinPoint pj,
                                          Method method,
                                          Class targetClass,
                                          Object target) throws Exception {
        JedisCluster cluster = null;
        try {
            CacheRoute annotation = method.getAnnotation(CacheRoute.class);
            // 根据路由规则名称获取路由规则
            String routerName = annotation.name();
            ICacheRouter router = RouteConfig.getCacheRouter(routerName);
            if (router == null) {
                throw new CMOSCacheBaseException(new CachePropertyException(("无法找到路由规则'" + routerName + "', 请检查配置文件!")));
            }
            // 计算路由参数
            AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
            String expr = annotation.param();
            EvaluationContext evaluationContext = this.createEvaluationContext(method, pj.getArgs(), target, targetClass);
            Object param = evaluator.key(expr, elementKey, evaluationContext);
            // 根据路由规则和路由参数进行路由
            String clusterName = router.getCluster(param);
            cluster = JedisClusterFactory.getJedisCluster(clusterName);
            if (cluster == null) {
                throw new CMOSCacheBaseException(new RouteException("根据缓存规则无法定位到缓存集群, 路由规则:" + routerName + ", 路由参数表达式:" + expr + ", 路由参数:" + param));
            }
        } catch (Exception e) {
            throw new CMOSCacheBaseException("执行缓存路由失败", e);
        }
        return cluster;
    }

    /**
     * 创建路由参数取值上下文
     * @param method      方法
     * @param args        参数
     * @param target      目标对象
     * @param targetClass 目标类
     * @return
     */
    private EvaluationContext createEvaluationContext(Method method, Object[] args, Object target, Class targetClass) {
        ExpressionRootObject rootObject = new ExpressionRootObject(null, method, args, target, targetClass);
        return new EvaluationContext(rootObject, method, args, new DefaultParameterNameDiscoverer());
    }

    /**
     * 获取目标类
     * @param target 对象
     * @return
     */
    private Class<?> getTargetClass(Object target) {
        Class targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }

}

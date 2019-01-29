package com.example.demo.cache.route.aspect;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 表达式取值上下文
 */
class EvaluationContext extends MethodBasedEvaluationContext {
    private final List<String> unavailableVariables = new ArrayList();

    EvaluationContext(Object rootObject, Method method, Object[] args, ParameterNameDiscoverer paramDiscoverer) {
        super(rootObject, method, args, paramDiscoverer);
    }

    public void addUnavailableVariable(String name) {
        this.unavailableVariables.add(name);
    }

    public Object lookupVariable(String name) {
        if(this.unavailableVariables.contains(name)) {
            throw new EvaluationException(name);
        } else {
            return super.lookupVariable(name);
        }
    }
}
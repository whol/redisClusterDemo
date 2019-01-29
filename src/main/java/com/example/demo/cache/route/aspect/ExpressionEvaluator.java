package com.example.demo.cache.route.aspect;

import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.Expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表达式求值
 *
 * @author zhuanghd
 * @since 1.0
 */
class ExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> keyCache = new ConcurrentHashMap(64);

    ExpressionEvaluator() {
    }

    public Object key(String keyExpression, AnnotatedElementKey methodKey, org.springframework.expression.EvaluationContext evalContext) {
        return this.getExpression(this.keyCache, methodKey, keyExpression).getValue(evalContext);
    }

}

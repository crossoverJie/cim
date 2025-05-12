package com.crossoverjie.cim.server.aspect;

import com.crossoverjie.cim.server.annotation.RedisLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author zhongcanyu
 * @date 2025/5/12
 * @description
 */
@Aspect
@Component
public class RedisLockAspect {

    @Autowired
    private RedissonClient redisson;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(com.crossoverjie.cim.server.annotation.RedisLock)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();
        RedisLock ann = method.getAnnotation(RedisLock.class);

        // 1) 解析 key 的 SpEL
        EvaluationContext ctx = new MethodBasedEvaluationContext(
                pjp.getThis(), method, pjp.getArgs(), nameDiscoverer);
        String lockKey = parser.parseExpression(ann.key())
                .getValue(ctx, String.class);

        // 2) 获取 Redisson 锁
        RLock lock = redisson.getLock(lockKey);

        boolean acquired = lock.tryLock(ann.waitTime(), ann.leaseTime(), TimeUnit.SECONDS);
        if (!acquired) {
            return null;
        }

        try {
            // 3) 执行业务方法
            return pjp.proceed();
        } finally {
            // 4) 释放锁
            lock.unlock();
        }
    }
}

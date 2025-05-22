package com.crossoverjie.cim.persistence.api.aspect;

import com.crossoverjie.cim.persistence.api.annotation.RedisLock;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RedisLockAspect {

    @Autowired
    private RedissonClient redisson;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(com.crossoverjie.cim.persistence.api.annotation.RedisLock)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();
        RedisLock ann = method.getAnnotation(RedisLock.class);

        EvaluationContext ctx = new MethodBasedEvaluationContext(
                pjp.getThis(), method, pjp.getArgs(), nameDiscoverer);
        String lockKey = parser.parseExpression(ann.key())
                .getValue(ctx, String.class);

        RLock lock = redisson.getLock(lockKey);

        boolean acquired = lock.tryLock(ann.waitTime(), ann.leaseTime(), TimeUnit.SECONDS);
        if (!acquired) {
            return null;
        }

        try {
            log.info("method: {} - lock acquired, key: {}", method.getName(), lockKey);
            return pjp.proceed();
        } finally {
            lock.unlock();
            log.info("method: {} - lock released, key: {}", method.getName(), lockKey);
        }
    }
}

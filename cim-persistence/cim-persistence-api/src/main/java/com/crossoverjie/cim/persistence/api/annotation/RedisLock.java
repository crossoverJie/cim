package com.crossoverjie.cim.persistence.api.annotation;

import java.lang.annotation.*;

/**
 * @author zhongcanyu
 * @date 2025/5/12
 * @description
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    /**
     * SpEL 表达式，用来计算锁的 key，例如 "#userId" 或 "T(java.lang.String).format('lock:user:%s', #userId)"
     */
    String key();

    /**
     * 最多等待锁的秒数
     */
    long waitTime() default 5;

    /**
     * 锁的自动过期（释放）秒数
     */
    long leaseTime() default 30;
}

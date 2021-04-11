package com.wangji92.springboot.idempotent.annotation;

import com.wangji92.springboot.idempotent.keygen.LockKeyGenerator;
import com.wangji92.springboot.idempotent.keygen.iml.DefaultLockKeyResolver;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 幂等唯一标识
 *
 * @author 汪小哥
 * @date 10-04-2021
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 有效期 默认：2
     *
     * @return expireTime
     */
    long expireTime() default 2L;

    /**
     * 获取锁等待的时间
     *
     * @return
     */
    long waitTime() default 0L;

    /**
     * 时间单位 默认：s
     *
     * @return TimeUnit
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 提示信息，可自定义
     *
     * @return String
     */
    String info() default "重复请求，请稍后重试";

    /**
     * 缓存key 前缀
     *
     * @return
     */
    String lockKeyPrefix() default "idempotent";

    /**
     * 是否解除当前key的锁定，否则过期后才能继续点击
     *
     * @return
     */
    boolean unlockKey() default true;

    /**
     * 生成锁 key 方式 默认为 sessionId +url
     *
     * @return
     */
    Class<? extends LockKeyGenerator> keyGenerator() default DefaultLockKeyResolver.class;
}

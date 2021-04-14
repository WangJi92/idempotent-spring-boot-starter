package com.wangji92.springboot.idempotent.interceptor;

import com.wangji92.springboot.idempotent.IdempotentProperties;
import com.wangji92.springboot.idempotent.annotation.Idempotent;
import com.wangji92.springboot.idempotent.exception.IdempotentException;
import com.wangji92.springboot.idempotent.keygen.LockKeyGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 防止重复提交
 *
 * @author 汪小哥
 * @date 10-04-2021
 */
@Component
public class IdempotentInterceptor implements HandlerInterceptor, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(IdempotentInterceptor.class);

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private IdempotentProperties idempotentProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Idempotent annotationIdempotent = handlerMethod.getMethod().getAnnotation(Idempotent.class);
            if (annotationIdempotent == null) {
                return true;
            }
            LockKeyGenerator keyGenerator = null;
            try {
                keyGenerator = applicationContext.getBean(annotationIdempotent.keyGenerator());
            } catch (BeansException e) {
                // ignore
            }
            if (keyGenerator == null) {
                logger.error("not found keyGenerator={} bean in spring project,Idempotent not ok", annotationIdempotent.keyGenerator().getSimpleName());
                return true;
            }
            String key = String.format("%s_%s", annotationIdempotent.lockKeyPrefix(), keyGenerator.resolverLockKey(annotationIdempotent, request, response, handlerMethod));
            RLock lock = redissonClient.getLock(key);

            if (lock.isLocked()) {
                throw new IdempotentException(annotationIdempotent.info());
            }

            boolean lockResult = lock.tryLock(annotationIdempotent.waitTime(), annotationIdempotent.expireTime(), annotationIdempotent.timeUnit());
            if (!lockResult) {
                throw new IdempotentException(annotationIdempotent.info());
            }
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Idempotent annotationIdempotent = handlerMethod.getMethod().getAnnotation(Idempotent.class);
            if (annotationIdempotent == null) {
                return;
            }
            LockKeyGenerator keyGenerator = null;
            try {
                keyGenerator = applicationContext.getBean(annotationIdempotent.keyGenerator());
            } catch (BeansException e) {
                //ignore
            }
            if (keyGenerator == null) {
                logger.error("not found keyGenerator={} bean in spring project,Idempotent not ok", annotationIdempotent.keyGenerator().getSimpleName());
                return;
            }
            String key = String.format("%s_%s", annotationIdempotent.lockKeyPrefix(), keyGenerator.resolverLockKey(annotationIdempotent, request, response, handlerMethod));
            RLock lock = redissonClient.getLock(key);
            //如果配置 不删除key 直到过期才自动取消
            if (lock.isHeldByCurrentThread() && annotationIdempotent.unlockKey()) {
                lock.unlock();
            }
        }


    }

    @Override
    public int getOrder() {
        Integer orderValue = idempotentProperties.getIdempotentInterceptorOrderValue();
        if (orderValue == null) {
            orderValue = Ordered.LOWEST_PRECEDENCE;
        }
        return orderValue;
    }
}

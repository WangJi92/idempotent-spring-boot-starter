package com.wangji92.springboot.idempotent.keygen.iml;

import com.wangji92.springboot.idempotent.annotation.Idempotent;
import com.wangji92.springboot.idempotent.keygen.LockKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * header -> cookie-> sessionId
 *
 * @author 汪小哥
 * @date 10-04-2021
 */
@Component(value = "idempotentDefaultLockKeyResolver")
public class DefaultLockKeyResolver implements LockKeyGenerator {
    /**
     * order 1  header
     */
    @Autowired
    private HeaderLockKeyResolver headerLockKeyResolver;

    /**
     * order 2  cookie
     */
    @Autowired
    private CookieLockKeyResolver cookieLockKeyResolver;

    /**
     * order 3 sessionId
     */
    @Autowired
    private SessionIdLockKeyResolver sessionIdLockKeyResolver;


    @Override
    public String resolverLockKey(Idempotent idempotent, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        // 先看header
        String lockKey = headerLockKeyResolver.resolverLockKey(idempotent, request, response, handlerMethod);
        if (!lockKey.contains(NOT_FOUND)) {
            return lockKey;
        }
        // 再看 cookie
        lockKey = cookieLockKeyResolver.resolverLockKey(idempotent, request, response, handlerMethod);
        if (!lockKey.contains(NOT_FOUND)) {
            return lockKey;
        }

        // 最后看sessionId
        lockKey = sessionIdLockKeyResolver.resolverLockKey(idempotent, request, response, handlerMethod);
        return lockKey;

    }
}

package com.wangji92.springboot.idempotent.keygen.iml;

import com.wangji92.springboot.idempotent.annotation.Idempotent;
import com.wangji92.springboot.idempotent.keygen.LockKeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * sessionId + getRequestURI
 *
 * @author 汪小哥
 * @date 10-04-2021
 */
@Component(value = "idempotentDefaultLockKeyResolver")
public class DefaultLockKeyResolver implements LockKeyGenerator {

    @Override
    public String resolverLockKey(Idempotent idempotent, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String sessionId = request.getSession().getId();
        return String.format("%s_%s", sessionId, request.getRequestURI());

    }
}

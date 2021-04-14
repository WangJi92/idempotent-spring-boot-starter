package com.wangji92.springboot.idempotent.keygen.iml;

import com.wangji92.springboot.idempotent.annotation.Idempotent;
import com.wangji92.springboot.idempotent.keygen.LockKeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * sessionId
 *
 * @author 汪小哥
 * @date 14-04-2021
 */
@Component
public class SessionIdLockKeyResolver implements LockKeyGenerator {
    @Override
    public String resolverLockKey(Idempotent idempotent, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String sessionId = request.getSession().getId();
        if (!StringUtils.hasText(sessionId)) {
            sessionId = NOT_FOUND;
        }
        return String.format("%s_%s", sessionId, request.getRequestURI());
    }
}

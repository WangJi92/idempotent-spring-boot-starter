package com.wangji92.springboot.idempotent.keygen.iml;

import com.wangji92.springboot.idempotent.IdempotentProperties;
import com.wangji92.springboot.idempotent.annotation.Idempotent;
import com.wangji92.springboot.idempotent.keygen.LockKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cookie
 *
 * @author 汪小哥
 * @date 14-04-2021
 */
@Component
public class CookieLockKeyResolver implements LockKeyGenerator {

    @Autowired
    private IdempotentProperties idempotentProperties;

    @Override
    public String resolverLockKey(Idempotent idempotent, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String lockKey = NOT_FOUND;
        if (StringUtils.hasText(idempotentProperties.getDefaultLockKeyCookieName())) {
            Cookie cookie = WebUtils.getCookie(request, idempotentProperties.getDefaultLockKeyCookieName());
            if (cookie != null) {
                lockKey = cookie.getValue();
            }
        }
        return String.format("%s_%s", lockKey, request.getRequestURI());
    }
}

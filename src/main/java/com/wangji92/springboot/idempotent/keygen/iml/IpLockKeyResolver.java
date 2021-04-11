package com.wangji92.springboot.idempotent.keygen.iml;

import com.wangji92.springboot.idempotent.annotation.Idempotent;
import com.wangji92.springboot.idempotent.keygen.LockKeyGenerator;
import com.wangji92.springboot.idempotent.utils.IpUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ip+getRequestURI 锁 锁定
 *
 * @author 汪小哥
 * @date 10-04-2021
 */
@Component(value = "idempotentIpLockKeyResolver")
public class IpLockKeyResolver implements LockKeyGenerator {

    @Override
    public String resolverLockKey(Idempotent idempotent, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String ip = IpUtils.getIpAddress(request);
        return String.format("%s_%s", ip, request.getRequestURI());
    }
}

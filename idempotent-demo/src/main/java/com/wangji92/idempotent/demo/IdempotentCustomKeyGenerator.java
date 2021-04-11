package com.wangji92.idempotent.demo;

import com.wangji92.springboot.idempotent.annotation.Idempotent;
import com.wangji92.springboot.idempotent.keygen.LockKeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义 锁key 处理器
 *
 * @author 汪小哥
 * @date 10-04-2021
 */
@Component
public class IdempotentCustomKeyGenerator implements LockKeyGenerator {
    @Override
    public String resolverLockKey(Idempotent idempotent, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {

        return handlerMethod.getMethod().getName();
    }
}

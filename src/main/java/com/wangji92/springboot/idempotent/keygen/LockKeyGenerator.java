package com.wangji92.springboot.idempotent.keygen;

import com.wangji92.springboot.idempotent.annotation.Idempotent;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 生成lock key
 *
 * @author 汪小哥
 * @date 10-04-2021
 */
public interface LockKeyGenerator {
    /**
     * 获取处理缓存key
     *
     * @param idempotent    注解
     * @param request       请求
     * @param response      响应
     * @param handlerMethod 方法
     * @return
     */
    String resolverLockKey(Idempotent idempotent, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);
}

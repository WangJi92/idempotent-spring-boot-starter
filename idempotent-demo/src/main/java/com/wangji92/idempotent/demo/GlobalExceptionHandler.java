package com.wangji92.idempotent.demo;

import com.wangji92.springboot.idempotent.exception.IdempotentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 定义全局异常
 *
 * @author 汪小哥
 * @date 10-04-2021
 */
@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 覆盖里面定义的错误异常 {@link com.wangji92.springboot.idempotent.IdempotentAutoConfiguration.IdempotentExceptionConfiguration#idempotentExceptionHandler(IdempotentException)}
     *
     * @param idempotentException
     * @return
     */
    @ExceptionHandler(value = {IdempotentException.class})
    @ResponseBody
    public ResponseEntity<String> idempotentExceptionHandler(IdempotentException idempotentException) {
        log.error("idempotent requestUrl={} sessionId={}", httpServletRequest.getRequestURI(), httpServletRequest.getSession().getId());
        String message = idempotentException.getMessage();
        message = "覆盖自定义全局异常" + message;
        return ResponseEntity.ok(message);
    }
}

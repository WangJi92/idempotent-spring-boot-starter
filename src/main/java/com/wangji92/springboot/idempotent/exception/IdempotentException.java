package com.wangji92.springboot.idempotent.exception;

/**
 * 如果出现重复提交异常处理
 *
 * @author 汪小哥
 * @date 10-04-2021
 */
public class IdempotentException extends RuntimeException {

    public IdempotentException() {
        super();
    }

    public IdempotentException(String message) {
        super(message);
    }
}

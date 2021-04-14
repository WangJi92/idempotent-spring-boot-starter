package com.wangji92.idempotent.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 汪小哥
 * @date 14-04-2021
 */
@Configuration
@Slf4j
public class DemoWebConfig implements WebMvcConfigurer {

    @Autowired
    private TestInterceptor testInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(testInterceptor);
        registration.order(testInterceptor.getOrder());
        registration.addPathPatterns("/**");
    }

    @Component
    public static class TestInterceptor implements HandlerInterceptor, Ordered {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            log.info("验证一下order 设置");
            return true;
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }
}

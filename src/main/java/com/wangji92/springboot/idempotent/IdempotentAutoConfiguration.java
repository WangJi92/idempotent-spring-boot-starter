package com.wangji92.springboot.idempotent;

import com.wangji92.springboot.idempotent.exception.IdempotentException;
import com.wangji92.springboot.idempotent.interceptor.IdempotentInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 配置处理
 *
 * @author 汪小哥
 * @date 10-04-2021
 */
@Configuration(proxyBeanMethods = false)
@ComponentScan(value = "com.wangji92.springboot.idempotent")
public class IdempotentAutoConfiguration {
    /**
     * 想自己定义异常的处理 自己处理吧
     */
    @ControllerAdvice
    @Order(value = Ordered.LOWEST_PRECEDENCE - 100)
    @Controller
    public static class IdempotentExceptionConfiguration {

        private static final Logger logger = LoggerFactory.getLogger(IdempotentExceptionConfiguration.class);

        @Autowired
        private HttpServletRequest httpServletRequest;


        @ExceptionHandler(value = {IdempotentException.class})
        @ResponseBody
        public ResponseEntity<String> idempotentExceptionHandler(IdempotentException idempotentException) {
            logger.info("idempotent requestUrl={} sessionId={}", httpServletRequest.getRequestURI(), httpServletRequest.getSession().getId());
            String message = idempotentException.getMessage();
            return ResponseEntity.ok(message);
        }
    }

    /**
     * 增加拦截器处理
     */
    @Configuration
    @Order(value = Ordered.LOWEST_PRECEDENCE - 100)
    public static class IdempotentWebConfig implements WebMvcConfigurer {

        @Autowired
        private IdempotentInterceptor idempotentInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(idempotentInterceptor).addPathPatterns("/**");

        }


        //region spring boot 5.x java 1.8 默认方法了 1.x 还不是
        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configurePathMatch(PathMatchConfigurer configurer) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addFormatters(FormatterRegistry registry) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addCorsMappings(CorsRegistry registry) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation returns {@code null}.
         */
        @Override
        @Nullable
        public Validator getValidator() {
            return null;
        }

        /**
         * {@inheritDoc}
         * <p>This implementation returns {@code null}.
         */
        @Override
        @Nullable
        public MessageCodesResolver getMessageCodesResolver() {
            return null;
        }
        //endregion
    }


}

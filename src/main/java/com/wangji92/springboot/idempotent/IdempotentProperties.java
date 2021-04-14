package com.wangji92.springboot.idempotent;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 配置属性
 *
 * @author 汪小哥
 * @date 14-04-2021
 */
@ConfigurationProperties(prefix = "spring.idempotent")
@Configuration
public class IdempotentProperties {
    /**
     * 不拦截的url 排除掉(逗号分割)
     */
    private String excludeUrls;
    /**
     * 进行拦截的url (逗号分割)
     */
    private String includeUrls = "/**";
    /**
     * 获取用户标识 header key 标识
     */
    private String defaultLockKeyHttpHeaderName;
    /**
     * 获取用户标识 cookie key 标识
     */
    private String defaultLockKeyCookieName;

    /**
     * 拦截器 顺序位置
     */
    private Integer idempotentInterceptorOrderValue = Ordered.LOWEST_PRECEDENCE;

    /**
     * 手动配置拦截器
     */
    private Boolean manualSettingIdempotentInterceptor = false;


    public void setIdempotentInterceptorOrderValue(Integer idempotentInterceptorOrderValue) {
        this.idempotentInterceptorOrderValue = idempotentInterceptorOrderValue;
    }


    public Boolean getManualSettingIdempotentInterceptor() {
        return manualSettingIdempotentInterceptor;
    }

    public void setManualSettingIdempotentInterceptor(Boolean manualSettingIdempotentInterceptor) {
        this.manualSettingIdempotentInterceptor = manualSettingIdempotentInterceptor;
    }

    public String getExcludeUrls() {
        return excludeUrls;
    }

    public void setExcludeUrls(String excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    public String getIncludeUrls() {
        return includeUrls;
    }

    public void setIncludeUrls(String includeUrls) {
        this.includeUrls = includeUrls;
    }

    public String getDefaultLockKeyHttpHeaderName() {
        return defaultLockKeyHttpHeaderName;
    }

    public void setDefaultLockKeyHttpHeaderName(String defaultLockKeyHttpHeaderName) {
        this.defaultLockKeyHttpHeaderName = defaultLockKeyHttpHeaderName;
    }

    public String getDefaultLockKeyCookieName() {
        return defaultLockKeyCookieName;
    }

    public void setDefaultLockKeyCookieName(String defaultLockKeyCookieName) {
        this.defaultLockKeyCookieName = defaultLockKeyCookieName;
    }

    public int getIdempotentInterceptorOrderValue() {
        return idempotentInterceptorOrderValue;
    }

    public void setIdempotentInterceptorOrderValue(int idempotentInterceptorOrderValue) {
        this.idempotentInterceptorOrderValue = idempotentInterceptorOrderValue;
    }
}

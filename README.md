# idempotent-spring-boot-starter

## 问题
为了防止重复提交，通常做法是：后端生成唯一的提交令牌（uuid），存储在服务端，页面在发起请求时，携带次令牌，后端验证请求后删除令牌，保证请求的唯一性。但是，上诉的做法是需要前后端都需要进行配合,而且不能防止当前请求还没有执行完成，继续点击的场景。


## 思路
### 基本思路
使用spring HandlerInterceptor 拦截+redission 提供的分布式锁来进行控制。


获取当前用户的标识+当前请求地址，作为一个唯一的key，去获取redis分布式锁。
如何获取前用户的标识：sessionId,token,ip 等等多种策略的获取唯一的key，可以采用不同的策略。


看了很多的博客，都是采用AOP去实现,为了防止重复提交一般都是针对web请求，采用拦截器处理足够用了(一般防止重复提交针对url+用户标识,不是特别需要针对body参数进行处理)，如果误用到其他的非web 线程的调用，会造成获取 httprequest 异常,而且感觉是有AOP 这种时候不太合适。


- [SpringBoot利用AOP防止请求重复提交](https://blog.csdn.net/a992795427/article/details/92834286)
- [spring boot 防止重复提交](https://blog.csdn.net/xiaoqiangyonghu/article/details/108661670)
- [Spring Boot 如何防止重复提交？](https://www.cnblogs.com/java-stack/p/11952190.html)



### 动手实践
如果实践一个  idempotent-spring-boot-starter
#### 分布式锁问题
分布式锁直接使用 redisson 即可。


- 基本配置
```java
spring.redis.host=127.0.0.1
spring.redis.port=6379
```

- maven 依赖

根据当前spring 的版本进行选择合适的依赖 redisson-spring-data 可以具体看官方文档。
redisson-spring-data module if necessary to support required Spring Boot version:
```xml
 <dependency>
   <groupId>org.redisson</groupId>
   <artifactId>redisson-spring-boot-starter</artifactId>
   <version>3.15.3</version>
</dependency>
```

- 更多配置可以参考链接

[https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter](https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter)
#### 分布式锁的key
获取前用户的标识,可以通过 sessionId,token,ip 等等多种策略的获取唯一的key，可以采用不同的策略，作为一个工具类可以提供不同的策略，或者自己定制一个分布式key的生成接口，注册到spring bean 即可。


[code link](https://github.com/WangJi92/idempotent-spring-boot-starter/blob/master/idempotent-demo/src/main/java/com/wangji92/idempotent/demo/IdempotentCustomKeyGenerator.java) 如下所示 根据方法的名称作为一个key 
```java
@Component
public class IdempotentCustomKeyGenerator implements LockKeyGenerator {
    @Override
    public String resolverLockKey(Idempotent idempotent, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {

        return handlerMethod.getMethod().getName();
    }
}

```
#### 异常返回如何处理？
一般情况下，每个工程项目中都会有定制化 统一的返回信息,默认情况下提供了一个全局的异常处理器，order 比较低,优先级比较低，可以自己定义一个order 等级比较高的spring boot 全局异常处理器，统一去处理。
[code link ](https://github.com/WangJi92/idempotent-spring-boot-starter/blob/master/src/main/java/com/wangji92/springboot/idempotent/IdempotentAutoConfiguration.java)
```java
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
```
#### 其他的细节

- 尝试获取锁的等待时间、锁的过期时间。
- 异常错误的提示信息。
- 业务执行完成后 是否解锁。

[code link](https://github.com/WangJi92/idempotent-spring-boot-starter/blob/master/src/main/java/com/wangji92/springboot/idempotent/annotation/Idempotent.java)
```java
@Inherited
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 有效期 默认：2
     *
     * @return expireTime
     */
    long expireTime() default 2L;

    /**
     * 获取锁等待的时间
     *
     * @return
     */
    long waitTime() default 0L;

    /**
     * 时间单位 默认：s
     *
     * @return TimeUnit
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 提示信息，可自定义
     *
     * @return String
     */
    String info() default "重复请求，请稍后重试";

    /**
     * 缓存key 前缀
     *
     * @return
     */
    String lockKeyPrefix() default "idempotent";

    /**
     * 是否解除当前key的锁定，否则过期后才能继续点击
     *
     * @return
     */
    boolean unlockKey() default true;

    /**
     * 生成锁 key 方式 默认为 sessionId +url
     *
     * @return
     */
    Class<? extends LockKeyGenerator> keyGenerator() default DefaultLockKeyResolver.class;
}
```


## 使用
[demo link](https://github.com/WangJi92/idempotent-spring-boot-starter/tree/master/idempotent-demo)
### maven 依赖
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- redisson-spring-boot-starter 非必须依赖 注意一下boot的版本-->
<!-- 存在 org.redisson.api.RedissonClient bean 即可-->
<dependency>
  <groupId>org.redisson</groupId>
  <artifactId>redisson-spring-boot-starter</artifactId>
  <version>3.15.3</version>
</dependency>
<dependency>
  <groupId>com.github.WangJi92</groupId>
  <artifactId>idempotent-spring-boot-starter</artifactId>
  <version>0.0.1</version>
</dependency>
```
### redission 配置
[https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter](https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter)

简单使用
```xml
spring.redis.host=127.0.0.1
spring.redis.port=6379
```
### 统一异常配置
* 配置统一异常处理器 针对性展示自己想要展示的异常信息格式
```java
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
```
### 注解使用
#### sessionId+uri
```java
@GetMapping("/testDefault")
@Idempotent(expireTime = 20L, waitTime = 0L, info = "错误错误", keyGenerator = DefaultLockKeyResolver.class, timeUnit = TimeUnit.SECONDS)
public ResponseEntity<String> testDefault() throws InterruptedException {
    logger.info("ok  testDefault session={} ip={}", request.getSession().getId(), IpUtils.getIpAddress(request));
    Thread.sleep(2000L);
    return ResponseEntity.ok("ok");
 }
```


```bash
# 先访问一下 获取到sessionId 看日志
curl  http://127.0.0.1:8080/testDefault

## Apache Brench 测试
# 把sessionId 替换一下 JSESSIONID=049F083CC7DCCACBA375A416C0A1FE2D
# 查看日志
ab -n 500 -c 50 -C JSESSIONID=049F083CC7DCCACBA375A416C0A1FE2D  http://127.0.0.1:8080/testDefault
```
#### ip+url
```java
@GetMapping("/testIp")
@Idempotent(expireTime = 20L, waitTime = 0L, info = "错误错误", keyGenerator = IpLockKeyResolver.class)
public ResponseEntity<String> testIp() throws InterruptedException {
    logger.info("ok testIp session={} ip={}", request.getSession().getId(), IpUtils.getIpAddress(request));
    Thread.sleep(2000L);
    return ResponseEntity.ok("ok");
}
```
```bash
## Apache Brench 测试
ab -n 500 -c 50    http://127.0.0.1:8080/testIp
```
#### 自定义key
```java
@GetMapping("/testCustom")
@Idempotent(expireTime = 20L, waitTime = 0L, info = "错误错误", keyGenerator = IdempotentCustomKeyGenerator.class)
public ResponseEntity<String> testCustom() throws InterruptedException {
    Thread.sleep(2000L);
    logger.info("ok testCustom session={} ip={}", request.getSession().getId(), IpUtils.getIpAddress(request));
    return ResponseEntity.ok("ok");
}
```
```bash
## Apache Brench 测试
ab -n 500 -c 50    http://127.0.0.1:8080/testCustom
```
#### 自定义可以+执行完成不释放锁
业务执行完成后不释放锁 unlockKey = false
```java
@GetMapping("/testCustomAndNotUnlockKey")
@Idempotent(expireTime = 20L, waitTime = 0L, info = "错误错误", keyGenerator = IdempotentCustomKeyGenerator.class, unlockKey = false)
public ResponseEntity<String> testCustomAndNotUnlockKey() throws InterruptedException {
    Thread.sleep(2000L);
    logger.info("ok testCustomAndNotUnlockKey session={} ip={}", request.getSession().getId(), IpUtils.getIpAddress(request));
    return ResponseEntity.ok("ok");
}
```
```bash
## Apache Brench 测试
ab -n 500 -c 50    http://127.0.0.1:8080/testCustomAndNotUnlockKey
```


## 参考文档

- [https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter](https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter)
- [https://blog.csdn.net/xiaoqiangyonghu/article/details/108661670](https://blog.csdn.net/xiaoqiangyonghu/article/details/108661670)
- [https://blog.csdn.net/a992795427/article/details/92834286](https://blog.csdn.net/a992795427/article/details/92834286)
- 并发模拟的三个工具:  [https://www.cnblogs.com/xusp/p/11845750.html](https://www.cnblogs.com/xusp/p/11845750.html)




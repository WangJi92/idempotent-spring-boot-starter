package com.wangji92.idempotent.demo;

import com.wangji92.springboot.idempotent.annotation.Idempotent;
import com.wangji92.springboot.idempotent.keygen.iml.DefaultLockKeyResolver;
import com.wangji92.springboot.idempotent.keygen.iml.IpLockKeyResolver;
import com.wangji92.springboot.idempotent.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author 汪小哥
 * @date 10-04-2021
 */
@RestController
public class IdempotentTestController {

    private static final Logger logger = LoggerFactory.getLogger(IdempotentTestController.class);

    @Autowired
    private HttpServletRequest request;

    /**
     * 测试默认key
     *
     * @return
     */
    @GetMapping("/testDefault")
    @Idempotent(expireTime = 20L, waitTime = 0L, info = "错误错误", keyGenerator = DefaultLockKeyResolver.class, timeUnit = TimeUnit.SECONDS)
    public ResponseEntity<String> testDefault() throws InterruptedException {
        logger.info("ok  testDefault session={} ip={}", request.getSession().getId(), IpUtils.getIpAddress(request));
        Thread.sleep(2000L);
        return ResponseEntity.ok("ok");
    }

    /**
     * 测试key  ip+url
     *
     * @return
     */
    @GetMapping("/testIp")
    @Idempotent(expireTime = 20L, waitTime = 0L, info = "错误错误", keyGenerator = IpLockKeyResolver.class)
    public ResponseEntity<String> testIp() throws InterruptedException {
        logger.info("ok testIp session={} ip={}", request.getSession().getId(), IpUtils.getIpAddress(request));
        Thread.sleep(2000L);
        return ResponseEntity.ok("ok");
    }

    /**
     * 测试 自定义key
     *
     * @return
     */
    @GetMapping("/testCustom")
    @Idempotent(expireTime = 20L, waitTime = 0L, info = "错误错误", keyGenerator = IdempotentCustomKeyGenerator.class)
    public ResponseEntity<String> testCustom() throws InterruptedException {
        Thread.sleep(2000L);
        logger.info("ok testCustom session={} ip={}", request.getSession().getId(), IpUtils.getIpAddress(request));
        return ResponseEntity.ok("ok");
    }

    /**
     * 执行完了也不释放锁
     *
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/testCustomAndNotUnlockKey")
    @Idempotent(expireTime = 20L, waitTime = 0L, info = "错误错误", keyGenerator = IdempotentCustomKeyGenerator.class, unlockKey = false)
    public ResponseEntity<String> testCustomAndNotUnlockKey() throws InterruptedException {
        Thread.sleep(2000L);
        logger.info("ok testCustomAndNotUnlockKey session={} ip={}", request.getSession().getId(), IpUtils.getIpAddress(request));
        return ResponseEntity.ok("ok");
    }


}

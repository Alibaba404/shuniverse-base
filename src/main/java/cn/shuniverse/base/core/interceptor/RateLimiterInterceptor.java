package cn.shuniverse.base.core.interceptor;

import cn.hutool.json.JSONUtil;
import cn.shuniverse.base.core.annotation.RateLimiter;
import cn.shuniverse.base.core.resp.R;
import cn.shuniverse.base.utils.IPUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.Clock;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by 蛮小满Sama at 2025-01-01 15:30
 *
 * @author 蛮小满Sama
 * @description 限流拦截器
 */
@Slf4j
@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    @Data
    static class RateLimitEntity {
        // 当前请求计数
        private final AtomicInteger count = new AtomicInteger(0);
        // 该计数器的过期时间（时间窗口结束时间）
        private volatile long expireTime;
    }

    // 限流响应码
    private static final int SC_TOO_MANY_REQUESTS = 429;
    // 请求计数器
    private final ConcurrentHashMap<String, RateLimitEntity> limiterMap = new ConcurrentHashMap<>();
    // 定时任务
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    // 初始化时启动定时清理任务
    public RateLimiterInterceptor() {
        // 延迟1分钟后，每5分钟清理一次过期key
        executorService.scheduleAtFixedRate(this::cleanExpiredKeys, 60, 300, TimeUnit.SECONDS);
    }

    /**
     * 清理过期的限流key（防止内存泄漏）
     */
    private void cleanExpiredKeys() {
        long currentTime = Clock.systemUTC().millis();
        // 遍历所有key，移除过期的
        limiterMap.entrySet().removeIf(entry -> currentTime > entry.getValue().getExpireTime());
        log.info("清理过期限流key完成，剩余key数量：{}", limiterMap.size());
    }

    /**
     * 销毁时关闭定时任务（避免线程泄漏）
     */
    @PreDestroy
    public void destroy() {
        executorService.shutdown();
        log.info("限流拦截器定时清理任务已关闭");
    }

    /**
     * 生成唯一限流key（解决重载方法问题）
     * 格式：类全限定名#方法名(参数类型1,参数类型2)-IP（若开启IP限流）
     */
    private String limitKey(HandlerMethod handlerMethod, HttpServletRequest request, RateLimiter limiter) {
        // 优先使用注解指定的key
        if (StringUtils.isNotBlank(limiter.key())) {
            String baseKey = limiter.key();
            if (limiter.ipLimit()) {
                baseKey += "-" + IPUtil.getClientIp(request);
            }
            return baseKey;
        }
        Method method = handlerMethod.getMethod();
        // 类全限定名（避免不同包下同名类冲突）
        String className = method.getDeclaringClass().getName();
        // 方法名 + 参数类型（解决重载方法冲突）
        String paramTypes = Arrays.stream(method.getParameterTypes())
                .map(Class::getName)
                .collect(Collectors.joining(","));
        String baseKey = String.format("%s#%s(%s)", className, method.getName(), paramTypes);
        // 若开启IP限流，拼接IP
        if (limiter.ipLimit()) {
            String ip = IPUtil.getClientIp(request);
            baseKey += "-" + ip;
        }
        return baseKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 排除非Controller方法（静态资源、404等）
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        // 2. 获取接口上的@RateLimit注解，无注解则放行
        RateLimiter limiter = handlerMethod.getMethodAnnotation(RateLimiter.class);
        if (limiter == null) {
            return true;
        }
        // 3. 生成唯一限流key（解决重载方法问题 + IP限流）
        String limitKey = limitKey(handlerMethod, request, limiter);
        long currentTime = Clock.systemUTC().millis();
        // 时间窗口（毫秒）
        long timeWindow = limiter.timeWindow();
        // 时间窗口内最大请求数
        int maxRequests = limiter.maxRequests();

        // 4. 获取或创建限流实体（并发安全）
        RateLimitEntity entity = limiterMap.computeIfAbsent(limitKey, k -> {
            RateLimitEntity newEntity = new RateLimitEntity();
            // 初始化过期时间：当前时间 + 时间窗口
            newEntity.setExpireTime(currentTime + timeWindow);
            return newEntity;
        });

        // 5. 检查是否已过期，过期则重置（每个key独立重置，解决全局重置问题）
        if (currentTime > entity.getExpireTime()) {
            // 加锁防止并发重置
            synchronized (entity) {
                // 双重检查（DCL）
                if (currentTime > entity.getExpireTime()) {
                    // 重置计数
                    entity.getCount().set(0);
                    // 重置过期时间
                    entity.setExpireTime(currentTime + timeWindow);
                }
            }
        }

        // 6. 计数+1，判断是否超过限流阈值
        int currentCount = entity.getCount().incrementAndGet();
        if (currentCount > maxRequests) {
            String ip = IPUtil.getClientIp(request);
            String uri = request.getRequestURI();
            log.warn("客户端IP【{}】触发限流，请求地址：{}，限流key：{}，当前计数：{}，阈值：{}", ip, uri, limitKey, currentCount, maxRequests);
            // 7. 返回标准429响应（HTTP状态码+JSON响应体）
            response.setStatus(SC_TOO_MANY_REQUESTS);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSONUtil.toJsonStr(R.failure(SC_TOO_MANY_REQUESTS, "达咩!太快了！")));
            return false;
        }
        return true;
    }
}

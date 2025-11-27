package cn.shuniverse.base.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 蛮小满Sama at 2025-11-27 09:53
 *
 * @author 蛮小满Sama
 * @description 限流注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    // 新增：自定义限流key（默认空，使用自动生成的key）
    String key() default "";

    /**
     * 时间窗口（毫秒），默认1000毫秒（1秒）
     */
    long timeWindow() default 1000;

    /**
     * 时间窗口内最大请求数，默认10次
     */
    int maxRequests() default 5;

    /**
     * 是否基于IP限流，默认true
     */
    boolean ipLimit() default false;
}

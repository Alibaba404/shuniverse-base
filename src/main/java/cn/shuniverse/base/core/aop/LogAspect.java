package cn.shuniverse.base.core.aop;


import cn.shuniverse.base.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Objects;

/**
 * Created by 蛮小满Sama at 2025-04-19 11:36
 *
 * @author 蛮小满Sama
 * @description
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
    private final LogService logService;

    @Autowired
    public LogAspect(LogService logService) {
        this.logService = logService;
    }

    /**
     * 定义切点：所有带有@Log注解的方法
     */
    @Pointcut("@annotation(cn.shuniverse.base.core.aop.Log)")
    public void pointCut() {
    }

    /**
     * 环绕通知
     *
     * @param point joinPoint
     */
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 当前时间
        long begin = Clock.systemUTC().millis();
        // 执行方法
        Object result = point.proceed();
        // 执行时长(毫秒)
        long time = Clock.systemUTC().millis() - begin;
        // 保存日志
        if (Objects.nonNull(logService)) {
            logService.logOperation(point, time, result, null);
        }
        return result;
    }

    /**
     * 异常通知
     *
     * @param point joinPoint
     * @param e     异常
     */
    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void afterThrowing(JoinPoint point, Throwable e) {
        if (Objects.nonNull(logService)) {
            logService.logOperation(point, 0, null, e);
        }
    }

}

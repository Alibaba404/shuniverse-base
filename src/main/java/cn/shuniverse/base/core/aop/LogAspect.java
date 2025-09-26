package cn.shuniverse.base.core.aop;


import cn.hutool.core.util.IdUtil;
import cn.shuniverse.base.entity.dto.LogDto;
import cn.shuniverse.base.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.LocalDateTime;

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
        // 生成唯一ID
        String traceId = IdUtil.getSnowflakeNextIdStr();
        long begin = Clock.systemUTC().millis();
        Object result = null;
        Throwable ex = null;
        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            ex = e;
            throw e;
        } finally {
            long time = Clock.systemUTC().millis() - begin;
            buildAndSaveLog(point, result, ex, time, traceId);
        }
    }

    private void buildAndSaveLog(JoinPoint point, Object result, Throwable ex, long time, String traceId) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        String description = logAnnotation != null ? logAnnotation.value() : "";
        boolean saveParams = logAnnotation != null && logAnnotation.saveParams();
        logService.logOperation(LogDto.builder()
                .traceId(traceId)
                .className(signature.getDeclaringTypeName())
                .methodName(method.getName())
                .args(saveParams ? point.getArgs() : null)
                .result(result)
                .time(time)
                .exception(ex)
                .description(description)
                .saveParams(saveParams)
                .executeAt(LocalDateTime.now())
                .build());
    }

}

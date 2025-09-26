package cn.shuniverse.base.service;

import org.aspectj.lang.JoinPoint;

/**
 * Created by 蛮小满Sama at 2025-06-23 16:40
 *
 * @author 蛮小满Sama
 * @description
 */
public interface LogService {
    void logSave(JoinPoint joinPoint, long time, Object result, Throwable ex);
}

package cn.shuniverse.base.service;

import org.aspectj.lang.JoinPoint;

/**
 * Created by 蛮小满Sama at 2025-06-23 16:40
 *
 * @author 蛮小满Sama
 * @description
 */
public interface LogService {
    /**
     * 日志操作接口（可打印，可进行入库）
     *
     * @param joinPoint 切点
     * @param time      请求耗时
     * @param result    请求响应结果
     * @param ex        异常
     */
    void logOperation(JoinPoint joinPoint, long time, Object result, Throwable ex);
}

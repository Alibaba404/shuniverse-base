package cn.shuniverse.base.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Service;

/**
 * Created by 蛮小满Sama at 2025-09-20 14:00
 *
 * @author 蛮小满Sama
 * @description 默认日志持久化实现
 */
@Slf4j
@Service
public class DefaultLogService implements LogService {
    @Override
    public void logOperation(JoinPoint point, long time, Object result, Throwable e) {
        // 空实现，什么都不做
        log.info("使用默认日志持久化实现,仅打印!请求耗时：{}ms，响应结果：{}", time, result);
    }
}

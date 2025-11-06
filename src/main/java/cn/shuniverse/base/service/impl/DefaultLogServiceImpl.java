package cn.shuniverse.base.service.impl;

import cn.hutool.json.JSONUtil;
import cn.shuniverse.base.entity.dto.LogDto;
import cn.shuniverse.base.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * Created by 蛮小满Sama at 2025-09-20 14:00
 *
 * @author 蛮小满Sama
 * @description 默认日志持久化实现
 * 核心：容器中没有 LogService 类型的Bean时才生效
 */
@Slf4j
@Service
@ConditionalOnMissingBean(LogService.class)
public class DefaultLogServiceImpl implements LogService {
    /**
     * 日志操作接口（可打印，可进行入库）
     *
     * @param logDto 日志对象
     */
    @Override
    public void logOperation(LogDto logDto) {
        // 空实现，什么都不做
        log.info("使用【shuniverse-base】默认日志实现！请求耗时：{}ms，响应结果：{}", logDto.getTime(), JSONUtil.toJsonStr(logDto));
    }
}

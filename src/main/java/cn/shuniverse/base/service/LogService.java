package cn.shuniverse.base.service;

import cn.shuniverse.base.entity.dto.LogDto;

/**
 * Created by 蛮小满Sama at 2025-06-23 16:40
 *
 * @author 蛮小满Sama
 * @description
 */
public interface LogService {
    /**
     * 日志操作接口（可打印，可进行入库）
     */
    void logOperation(LogDto logDto);
}

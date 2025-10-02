package cn.shuniverse.base.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by 蛮小满Sama at 2025-06-23 16:09
 *
 * @author 蛮小满Sama
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogDto {

    private String traceId;
    /**
     * 方法签名
     */
    private String methodName;

    /**
     * 类名
     */
    private String className;

    /**
     * 参数
     */
    private Object[] args;

    /**
     * 返回值
     */
    private Object result;

    /**
     * 耗时（毫秒）
     */
    private long time;

    /**
     * 异常信息
     */
    private Throwable exception;

    /**
     * 注解中的描述
     */
    private String description;

    /**
     * 是否保存参数
     */
    private boolean saveParams;

    /**
     * 执行时间戳
     */
    private LocalDateTime executeAt;

    /**
     * 执行IP
     */
    private String ip;
}

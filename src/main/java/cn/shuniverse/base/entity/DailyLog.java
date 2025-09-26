package cn.shuniverse.base.entity;

import cn.shuniverse.base.constants.DateConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 蛮小满Sama at 2025-06-23 14:55
 *
 * @author 蛮小满Sama
 * @description
 */
@Data
@Accessors(chain = true)
@Schema(name = "DailyLog", description = "日志")
public class DailyLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "创建人")
    private String createdBy;
    @Schema(description = "更新人")
    private String updatedBy;
    @Schema(description = "版本")
    private Long version;
    @Schema(description = "删除标识")
    private Integer deleted;
    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = DateConstants.DATETIME_FORMAT)
    @JsonFormat(pattern = DateConstants.DATETIME_FORMAT, timezone = DateConstants.GMT8)
    private Date createdAt;
    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = DateConstants.DATETIME_FORMAT)
    @JsonFormat(pattern = DateConstants.DATETIME_FORMAT, timezone = DateConstants.GMT8)
    private Date updatedAt;
    @Schema(description = "操作名称")
    private String operation;
    @Schema(description = "方法名")
    private String method;
    @Schema(description = "请求参数")
    private String params;
    @Schema(description = "操作用户ID")
    private String userId;
    @Schema(description = "操作用户名")
    private String username;
    @Schema(description = "IP地址")
    private String ip;
    @Schema(description = "操作状态（0失败 1成功）")
    private Byte status;
    @Schema(description = "错误消息")
    private String errorMsg;
    @Schema(description = "操作耗时")
    private Long timeMillis;
    @Schema(description = "操作结果")
    private String results;
}

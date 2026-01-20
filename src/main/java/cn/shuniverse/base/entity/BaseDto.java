package cn.shuniverse.base.entity;

import cn.shuniverse.base.constants.DateConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 蛮小满Sama at 2026-01-20 15:57
 *
 * @author 蛮小满Sama
 * @description DTO的基础类
 */
@Data
@Schema(description = "基础DTO")
@Accessors(chain = true)
public class BaseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    protected String id;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = DateConstants.GMT8, pattern = DateConstants.DATETIME_FORMAT)
    @Schema(description = "创建时间")
    protected Date createdAt;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = DateConstants.GMT8, pattern = DateConstants.DATETIME_FORMAT)
    @Schema(description = "更新时间")
    protected Date updatedAt;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    protected String createdBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    protected String updatedBy;
}


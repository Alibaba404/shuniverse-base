package cn.shuniverse.base.entity;

import cn.shuniverse.base.constants.DateConstants;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Created by 蛮小满Sama at 2025-12-24 15:43
 *
 * @author 蛮小满Sama
 * @description 基础DTO
 */
@Data
public class BaseDto {
    /**
     * 主键
     */
    private String id;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = DateConstants.GMT8, pattern = DateConstants.DATETIME_FORMAT)
    protected Date createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = DateConstants.GMT8, pattern = DateConstants.DATETIME_FORMAT)
    protected Date updatedAt;

    /**
     * 创建人
     */
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    protected String createdBy;

    /**
     * 更新人
     */
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    protected String updatedBy;
}

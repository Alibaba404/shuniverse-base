package cn.shuniverse.base.entity;

import cn.shuniverse.base.constants.DateConstants;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 蛮小满Sama at 2025-06-18 13:20
 *
 * @author 蛮小满Sama
 * @description 基类
 */
@Data
@Accessors(chain = true)
public class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    protected String id;
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(timezone = DateConstants.GMT8, pattern = DateConstants.DATETIME_FORMAT)
    protected Date createdAt;
    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(timezone = DateConstants.GMT8, pattern = DateConstants.DATETIME_FORMAT)
    protected Date updatedAt;
    /**
     * 逻辑删除
     */
    @TableLogic
    protected Integer deleted;

    @Version
    protected Integer version;

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

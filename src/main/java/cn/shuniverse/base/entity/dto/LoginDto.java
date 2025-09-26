package cn.shuniverse.base.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created by 蛮小满Sama at 2025-06-23 16:09
 *
 * @author 蛮小满Sama
 * @description
 */
@Data
@Accessors(chain = true)
@Schema(name = "LoginDto", description = "登录成功信息")
public class LoginDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "uid")
    private String uid;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "用户名称")
    private String username;
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "性别：0-女，1-男，2-保密")
    private Integer gender;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "最近一次登录IP")
    private String lastLoginIp;
}

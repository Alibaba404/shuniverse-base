package cn.shuniverse.base.entity.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created by 蛮小满Sama at 2025-06-23 14:27
 *
 * @author 蛮小满Sama
 * @description
 */
@Data
@Schema(name = "CaptchaPo", description = "验证码参数")
public class CaptchaPo {
    @Schema(description = "验证码图片宽度", defaultValue = "130px")
    private Integer width = 130;
    @Schema(description = "验证码图片高度", defaultValue = "48px")
    private Integer height = 48;
    @Schema(description = "邮箱")
    private String email;
}

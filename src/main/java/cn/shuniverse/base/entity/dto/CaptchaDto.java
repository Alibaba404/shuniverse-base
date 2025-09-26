package cn.shuniverse.base.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 蛮小满Sama at 2025-06-23 14:23
 *
 * @author 蛮小满Sama
 * @description
 */
@Data
@Schema(name = "CaptchaDto", description = "验证码")
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaDto {
    @Schema(description = "验证码key")
    private String key;
    @Schema(description = "验证码图片(Base64)")
    private String image;
}

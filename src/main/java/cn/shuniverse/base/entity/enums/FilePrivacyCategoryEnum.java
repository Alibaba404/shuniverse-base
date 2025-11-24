package cn.shuniverse.base.entity.enums;

import lombok.Getter;

/**
 * Created by 蛮小满Sama at 2024-09-13 09:44
 *
 * @author 蛮小满Sama
 * @description
 */
@Getter
public enum FilePrivacyCategoryEnum {
    PUBLIC("PUBLIC", "公开文件"),
    PRIVATE("PRIVATE", "权限文件"),
    COMMON("COMMON", "通用文件"),
    SHARE("SHARE", "分享文件"),
    TEMP("TEMP", "临时文件"),
    ;

    FilePrivacyCategoryEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final String code;
    private final String desc;
}

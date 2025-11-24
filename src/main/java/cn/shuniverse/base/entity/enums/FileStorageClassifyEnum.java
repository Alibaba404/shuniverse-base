package cn.shuniverse.base.entity.enums;

import lombok.Getter;

/**
 * Created by 蛮小满Sama at 2025-11-24 10:12
 *
 * @author 蛮小满Sama
 * @description
 */
@Getter
public enum FileStorageClassifyEnum {
    LOCAL("local", "本地存储"),
    MINIO("minio", "MINIO"),
    ;

    FileStorageClassifyEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final String code;
    private final String desc;
}

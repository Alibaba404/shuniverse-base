package cn.shuniverse.base.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by 蛮小满Sama at 2025-11-26 11:53
 *
 * @author 蛮小满Sama
 * @description 对象存储DTO
 */
@Data
@Accessors(chain = true)
public class ObjectStorageDto {
    // 原始文件名
    private String originFileName;
    // 文件名
    private String filename;
    // 文件路径
    private String filePath;
    // 访问地址
    private String fileUrl;
    // 文件大小（单位：Byte）
    private long fileSize;
    // 文件类型
    private String fileType;
    // 文件MD5
    private String fileMd5;
}

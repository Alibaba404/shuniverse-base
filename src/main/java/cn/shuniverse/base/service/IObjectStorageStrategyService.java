package cn.shuniverse.base.service;

import cn.shuniverse.base.entity.dto.ObjectStorageDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * Created by 蛮小满Sama at 2025-11-24 09:59
 *
 * @author 蛮小满Sama
 * @description 对象存储服务
 */
public interface IObjectStorageStrategyService {
    String sport();

    /**
     * 文件上传
     *
     * @param file 文件对象
     * @return 文件路径
     */
    ObjectStorageDto uploadCommon(MultipartFile file, String buildFilePath);

    ObjectStorageDto uploadPrivate(MultipartFile file);

    ObjectStorageDto uploadPublic(MultipartFile file);

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return true/false
     */
    boolean delete(String filePath);

    File download(String filePath);

    /**
     * 下载文件方法
     *
     * @param filePath 文件名
     * @return InputStream
     */
    InputStream downloadInputStream(String filePath);

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    default long getFileSize(String filePath) {
        return 0;
    }
}

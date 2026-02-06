package cn.shuniverse.base.service;

import cn.shuniverse.base.entity.dto.ObjectStorageDto;
import cn.shuniverse.base.utils.FilePathUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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
     * @param file     MultipartFile文件对象
     * @param filePath 文件路径（不含 bucket）
     * @return 文件路径
     */
    ObjectStorageDto upload(MultipartFile file, String filePath) throws IOException;

    /**
     * 文件上传
     *
     * @param file     File文件对象
     * @param filePath 文件路径（不含 bucket）
     * @return 文件路径
     */
    ObjectStorageDto upload(File file, String filePath);

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


    /**
     * 构建文件路径
     *
     * @param filename           文件名
     * @param uid                用户id
     * @param time               时间
     * @param filePermissionPath 文件权限路径
     * @return 文件路径
     */
    default String buildFilePath(String filename, String uid, Date time, String filePermissionPath) {
        return FilePathUtil.buildPathCommon(filename, uid, time, null, filePermissionPath);
    }
}

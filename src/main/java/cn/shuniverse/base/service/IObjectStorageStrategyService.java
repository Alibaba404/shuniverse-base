package cn.shuniverse.base.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.shuniverse.base.entity.dto.ObjectStorageDto;
import cn.shuniverse.base.utils.FilePathUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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


    default String buildFilePath(String filename, String fileSourceCategory) {
        return buildFilePath(filename, null, fileSourceCategory);
    }

    default String buildFilePath(String filename, String formatter, String fileSourceCategory) {
        return buildFilePath(filename, null, formatter, fileSourceCategory);
    }

    default String buildFilePath(String filename, String uid, String formatter, String fileSourceCategory) {
        return buildFilePath(filename, uid, null, formatter, fileSourceCategory);
    }


    /**
     * 构建文件路径
     *
     * @param filename           文件名
     * @param uid                用户id
     * @param time               时间
     * @param formatter          时间格式
     * @param fileSourceCategory 文件权限
     * @return 文件路径
     */
    default String buildFilePath(String filename, String uid, Date time, String formatter, String fileSourceCategory) {
        return FilePathUtil.buildFilePath(filename, uid, time, formatter, fileSourceCategory);
    }
}

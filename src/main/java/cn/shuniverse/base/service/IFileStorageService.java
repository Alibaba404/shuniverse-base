package cn.shuniverse.base.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.shuniverse.base.entity.enums.FilePrivacyCategoryEnum;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.RegionConflictException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 蛮小满Sama at 2025-11-24 09:59
 *
 * @author 蛮小满Sama
 * @description
 */
public interface IFileStorageService {
    String sport();

    default String buildFilePath(String fileName, Date time, String fileSourceCategory) {
        if (null == time) {
            time = DateUtil.date();
        }
        // 1.获取文件后缀
        String extensionName = FileUtil.extName(fileName);
        // 日期转文件夹
        String dateString = DateUtil.format(time, "yyyy/MM/dd");
        // 3.拼接存储路径
        return String.format("/%s/%s/%s.%s", fileSourceCategory, dateString, IdUtil.fastSimpleUUID(), extensionName);
    }

    default String buildFilePath(String fileName, Date time) {
        return buildFilePath(fileName, time, FilePrivacyCategoryEnum.PRIVATE.getCode());
    }

    /**
     * 获取COS文件短路径
     *
     * @param url 文件地址
     * @return 不含bucket的短路径
     */
    String getCosShortPath(String url);

    /**
     * 获取COS文件短路径
     *
     * @param url    文件地址
     * @param bucket 桶名称
     * @return 不含bucket的短路径
     */
    default String getCosShortPath(String url, String bucket) {
        // 创建匹配器对象
        Matcher m = Pattern.compile(bucket + "(.*)").matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * 文件上传
     *
     * @param file 文件对象
     * @return 文件路径
     */
    String uploadCommon(MultipartFile file, String buildFilePath) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, RegionConflictException,
            InvalidExpiresRangeException;

    String upload(MultipartFile file) throws RegionConflictException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException
            , NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidExpiresRangeException;

    String uploadPublic(MultipartFile file) throws RegionConflictException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidExpiresRangeException;

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return true/false
     */
    boolean deleteFile(String filePath);

    File downloadFile(String filePath);

    /**
     * 下载文件方法
     *
     * @param objectName 文件名
     * @return InputStream
     */
    InputStream downloadInputStream(String objectName) throws ServerException,
            InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

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

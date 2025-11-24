package cn.shuniverse.base.service.impl;

import cn.shuniverse.base.entity.enums.FileStorageClassifyEnum;
import cn.shuniverse.base.service.IFileStorageService;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 蛮小满Sama at 2025-11-24 10:17
 *
 * @author 蛮小满Sama
 * @description
 */
@Service
public class DefaultFileStorageServiceImpl implements IFileStorageService {
    @Override
    public String sport() {
        return FileStorageClassifyEnum.LOCAL.getCode();
    }

    /**
     * 获取COS文件短路径
     *
     * @param url 文件地址
     * @return 不含bucket的短路径
     */
    @Override
    public String getCosShortPath(String url) {
        return getCosShortPath(url, "");
    }

    /**
     * 文件上传
     *
     * @param file          文件对象
     * @param buildFilePath
     * @return 文件路径
     */
    @Override
    public String uploadCommon(MultipartFile file, String buildFilePath) {
        return "";
    }

    @Override
    public String upload(MultipartFile file) {
        return "";
    }

    @Override
    public String uploadPublic(MultipartFile file) {
        return "";
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return true/false
     */
    @Override
    public boolean deleteFile(String filePath) {
        return false;
    }

    @Override
    public File downloadFile(String filePath) {
        return null;
    }

    /**
     * 下载文件方法
     *
     * @param objectName 文件名
     * @return InputStream
     */
    @Override
    public InputStream downloadInputStream(String objectName) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return null;
    }
}

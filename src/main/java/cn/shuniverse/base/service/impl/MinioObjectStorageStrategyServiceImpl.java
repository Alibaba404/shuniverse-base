package cn.shuniverse.base.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import cn.shuniverse.base.core.exception.BisException;
import cn.shuniverse.base.core.resp.RCode;
import cn.shuniverse.base.entity.dto.ObjectStorageDto;
import cn.shuniverse.base.entity.enums.FileStorageClassifyEnum;
import cn.shuniverse.base.service.IObjectStorageStrategyService;
import cn.shuniverse.base.utils.CapacityUtil;
import cn.shuniverse.base.utils.FileCustomUtil;
import cn.shuniverse.base.utils.MinioUtil;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 蛮小满Sama at 2025-11-24 10:03
 *
 * @author 蛮小满Sama
 * @description
 */
@Slf4j
@Service
public class MinioObjectStorageStrategyServiceImpl implements IObjectStorageStrategyService {

    // 账号
    @Value("${minio.access-key}")
    private String accessKey;
    // 密码
    @Value("${minio.secret-key}")
    private String secretKey;
    // 访问地址
    @Value("${minio.endpoint}")
    private String endpoint;
    // 存储Bucket名称
    @Value("${minio.bucket}")
    private String bucket;
    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        if (this.minioClient == null) {
            this.minioClient = MinioUtil.minioClient(accessKey, secretKey, endpoint);
        }
    }


    @Override
    public String sport() {
        return FileStorageClassifyEnum.MINIO.getCode();
    }

    private String getCosShortPath(String url, String bucket) {
        // 创建匹配器对象
        Matcher m = Pattern.compile(bucket + "(.*)").matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * 获取COS文件短路径
     *
     * @param url 文件地址
     * @return 不含bucket的短路径
     */
    public String getCosShortPath(String url) {
        return getCosShortPath(url, bucket);
    }

    private String getFilename(String fileUrl) {
        if (fileUrl != null) {
            return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }
        return null;
    }

    /**
     * 构建通用返回
     *
     * @param fileMimeType         文件类型
     * @param fileMd5              文件md5
     * @param fileOriginalFilename 原文件名
     * @param fileUrl              文件地址
     * @param fileSize             文件大小
     * @return 上传文件信息对象
     */
    private ObjectStorageDto buildDto(String fileMimeType, String fileMd5, String fileOriginalFilename, String fileUrl, Long fileSize) {
        log.info("文件MD5：{}", fileMd5);
        return new ObjectStorageDto()
                .setFileType(fileMimeType)
                .setFileMd5(fileMd5)
                .setOriginFileName(fileOriginalFilename)
                .setFilename(this.getFilename(fileUrl))
                .setFilePath(this.getCosShortPath(fileUrl))
                .setFileUrl(fileUrl)
                .setFileSize(fileSize);
    }

    /**
     * 文件MD5计算
     *
     * @param fileSize    文件大小
     * @param inputStream 文件流
     * @return 文件MD5
     */
    private String fileMd5Calculator(long fileSize, InputStream inputStream) {
        // 文件大小MB
        double mb = CapacityUtil.bytesToMb(fileSize);
        // 小于 10MB才自动计算md5
        if (mb <= 10) {
            try {
                return FileCustomUtil.fileMd5(inputStream);
            } catch (NoSuchAlgorithmException | IOException e) {
                return "-1";
            }
        }
        return "-1";
    }

    private String uploadCommon(InputStream inputStream, String fileMimeType, Long fileSize, String filePath) {
        try {
            // 文件的MIME类型
            if (StringUtils.isBlank(fileMimeType)) {
                fileMimeType = "text/plain";
            }
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    // 文件短路径
                    .object(filePath)
                    // 文件类型
                    .contentType(fileMimeType)
                    // 存储桶名
                    .bucket(bucket)
                    // 文件流，以及大小，-1代表不分片
                    .stream(inputStream, fileSize, -1)
                    .build();
            //执行上传
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(putObjectArgs);
            log.info("上传结果: {}", objectWriteResponse.object());
            //上传之后的文件地址是：
            String fileUrl = String.format("%s/%s%s", endpoint, bucket, filePath);
            log.info("文件地址 :{}", fileUrl);
            return fileUrl;
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("文件上传失败！QaQ", e);
            throw BisException.me(RCode.FILE_UPLOAD_ERROR);
        }
    }


    /**
     * 文件上传
     *
     * @param file     文件对象
     * @param filePath 文件路径
     * @return 文件路径
     */
    @Override
    public ObjectStorageDto upload(MultipartFile file, String filePath) throws IOException {
        if (null == file) {
            throw BisException.me(RCode.FILE_EMPTY);
        }
        String fileUrl;
        try {
            // 1. 检查 Bucket 是否存在，不存在则创建
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            fileUrl = this.uploadCommon(file.getInputStream(), file.getContentType(), file.getSize(), filePath);
        } catch (Exception e) {
            log.error("文件上传失败！QaQ", e);
            throw BisException.me(RCode.FILE_UPLOAD_ERROR);
        }
        return buildDto(file.getContentType(), this.fileMd5Calculator(file.getSize(), file.getInputStream()), file.getOriginalFilename(), fileUrl, file.getSize());
    }

    /**
     * 文件上传（公共）
     *
     * @param file     文件对象
     * @param filePath 文件短路径（不含 bucket）
     * @return 文件上传信息对象
     */
    @Override
    public ObjectStorageDto upload(File file, String filePath) {
        if (null == file) {
            throw BisException.me(RCode.FILE_EMPTY);
        }
        String fileMimeType = FileUtil.getMimeType(file.getAbsolutePath());
        long fileSize = FileUtil.size(file);
        String fileUrl;
        try {
            // 1. 检查 Bucket 是否存在，不存在则创建
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            fileUrl = this.uploadCommon(FileUtil.getInputStream(file), fileMimeType, fileSize, filePath);
        } catch (Exception e) {
            log.error("文件上传失败！QaQ", e);
            throw BisException.me(RCode.FILE_UPLOAD_ERROR);
        }
        String fileMd5 = this.fileMd5Calculator(fileSize, IoUtil.toStream(file));
        return buildDto(fileMimeType, fileMd5, file.getName(), fileUrl, fileSize);
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return true/false
     */
    @Override
    public boolean delete(String filePath) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .object(filePath)
                    .bucket(bucket)
                    .build();
            minioClient.removeObject(removeObjectArgs);
            log.info("文件「{}」删除成功!", filePath);
            return true;
        } catch (ErrorResponseException | InsufficientDataException
                 | InternalException | InvalidBucketNameException
                 | InvalidKeyException | InvalidResponseException
                 | IOException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {
            log.error("文件「{}」删除失败!Message:{}", filePath, e.getMessage());
            throw BisException.me(RCode.FILE_DELETE_ERROR);
        }
    }

    @Override
    public File download(String filePath) {
        try {
            File tempFile = FileUtil.file(filePath);
            minioClient.downloadObject(DownloadObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .filename(tempFile.getAbsolutePath())
                    .build());
            return tempFile;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {
            log.error("文件「{}」下载失败!Message:{}", filePath, e.getMessage());
            throw BisException.me(RCode.FILE_DOWNLOAD_ERROR);
        }
    }

    /**
     * 下载文件方法
     *
     * @param filePath 文件名
     * @return InputStream
     */
    @Override
    public InputStream downloadInputStream(String filePath) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(filePath)
                            .build()
            );
        } catch (Exception e) {
            log.error("文件「{}」下载失败!", filePath, e);
            throw BisException.me(RCode.FILE_DOWNLOAD_ERROR);
        }
    }

    @Override
    public long getFileSize(String filePath) {
        try {
            ObjectStat statObject = minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(filePath).build());
            return statObject.length();
        } catch (Exception e) {
            log.error("文件「{}」获取文件大小失败!", filePath, e);
        }
        return 0;
    }


    /**
     * 设置权限
     */
    public void setBucketPublicReadPolicy(MinioClient instance, String bucketString, String allowUserName) throws ServerException,
            InvalidBucketNameException, InsufficientDataException,
            ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        String policy = """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": {
                                "AWS": ["arn:aws:iam:::user/#{allowUserName}"]
                            },
                            "Action": [
                                "s3:DeleteObject",
                                "s3:GetObject",
                                "s3:ListBucket",
                                "s3:PutObject"
                            ],
                            "Resource": [
                                "arn:aws:s3:::#{bucket}",
                                "arn:aws:s3:::#{bucket}/*"
                            ]
                        },
                        {
                            "Effect": "Allow",
                            "Principal": {
                                "AWS": ["arn:aws:iam:::user/#{allowUserName}"]
                            },
                            "Action": [
                                "s3:ListAllMyBuckets"
                            ],
                            "Resource": [
                                "arn:aws:s3:::*"
                            ]
                        },
                        {
                            "Effect": "Allow",
                            "Action": [
                                "s3:GetObject"
                            ],
                            "Principal": {
                                "AWS": ["*"]
                            },
                            "Resource": [
                                "arn:aws:s3:::#{bucket}/PUBLIC/*"
                            ]
                        }
                    ]
                }
                """
                .replace("#{bucket}", bucketString)
                .replace("#{allowUserName}", allowUserName);
        String jsonStr = JSONUtil.toJsonStr(policy);
        log.info("bucket: {}, policy: {}", bucketString, jsonStr);
        instance.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketString).config(jsonStr).build());
    }
}

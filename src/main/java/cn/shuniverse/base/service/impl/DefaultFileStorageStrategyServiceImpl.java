package cn.shuniverse.base.service.impl;

import cn.shuniverse.base.entity.dto.ObjectStorageDto;
import cn.shuniverse.base.entity.enums.FileStorageClassifyEnum;
import cn.shuniverse.base.service.IObjectStorageStrategyService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * Created by 蛮小满Sama at 2025-11-24 10:17
 *
 * @author 蛮小满Sama
 * @description
 */
@Service
public class DefaultFileStorageStrategyServiceImpl implements IObjectStorageStrategyService {
    @Override
    public ObjectStorageDto upload(File file, String filePath) {
        return null;
    }

    @Override
    public String sport() {
        return FileStorageClassifyEnum.LOCAL.getCode();
    }


    /**
     * 文件上传
     *
     * @param file     文件对象
     * @param filePath 文件路径
     * @return 文件路径
     */
    @Override
    public ObjectStorageDto upload(MultipartFile file, String filePath) {
        return null;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return true/false
     */
    @Override
    public boolean delete(String filePath) {
        return false;
    }

    @Override
    public File download(String filePath) {
        return null;
    }

    /**
     * 下载文件方法
     *
     * @param filePath 文件名
     * @return InputStream
     */
    @Override
    public InputStream downloadInputStream(String filePath) {
        return null;
    }
}

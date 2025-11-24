package cn.shuniverse.base.service;

import cn.shuniverse.base.service.impl.DefaultFileStorageServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 蛮小满Sama at 2025-11-24 10:15
 *
 * @author 蛮小满Sama
 * @description 文件存储策略
 */
@Component
public class FileStorageStrategy {

    private final List<IFileStorageService> fileStorageServices;

    public FileStorageStrategy(List<IFileStorageService> fileStorageServices) {
        this.fileStorageServices = fileStorageServices;
    }

    /**
     * 获取文件存储服务
     *
     * @param fileStorageClassifyString 文件存储分类
     * @return 文件存储服务
     */
    public IFileStorageService peek(String fileStorageClassifyString) {
        return fileStorageServices.stream()
                .filter(fileStorageService -> fileStorageService.sport().equals(fileStorageClassifyString))
                .findFirst()
                .orElse(new DefaultFileStorageServiceImpl());
    }
}

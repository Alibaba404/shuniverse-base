package cn.shuniverse.base.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 蛮小满Sama at 2025-09-26 16:35
 *
 * @author 蛮小满Sama
 * @description Tika 文件解析服务
 */
@Slf4j
@Service
public class TikaService {
    private final Tika tika;

    @Autowired
    public TikaService(Tika tika) {
        this.tika = tika;
    }

    /**
     * 文件解析结果 DTO
     */
    @Data
    public static class TikaResult {
        private String mimeType;
        private String text;
        private Map<String, String> metadata;
    }

    /**
     * 获取 MIME 类型
     */
    public String detectMimeType(MultipartFile file) {
        return tika.detect(file.getOriginalFilename());
    }

    /**
     * 仅提取文本
     */
    public String justExtractText(InputStream inputStream) throws IOException, TikaException {
        return tika.parseToString(inputStream);
    }


    /**
     * 提取元数据和文件内容
     */
    public TikaResult extractMetadataAndContent(InputStream inputStream, String filename) throws TikaException, IOException {
        TikaResult result = new TikaResult();
        Metadata metadata = new Metadata();
        metadata.set("resourceName", filename);
        // 获取文件内容
        String fileContent = tika.parseToString(inputStream, metadata);
        Map<String, String> metaDataMap = new HashMap<>();
        for (String name : metadata.names()) {
            metaDataMap.put(name, metadata.get(name));
        }
        result.setMimeType(metadata.get("Content-Type"));
        result.setMetadata(metaDataMap);
        result.setText(fileContent);
        return result;
    }

    /**
     * 综合解析：类型 + 文本 + 元数据
     */
    public TikaResult parseAll(MultipartFile file) throws TikaException, IOException {
        return extractMetadataAndContent(file.getInputStream(), file.getOriginalFilename());
    }
}

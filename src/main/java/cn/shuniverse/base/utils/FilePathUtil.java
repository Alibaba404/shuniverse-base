package cn.shuniverse.base.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * Created by 蛮小满Sama at 2026-01-23 14:48
 *
 * @author 蛮小满Sama
 * @description
 */
public class FilePathUtil {
    // 默认模板路径
    private static String templatePath = "/{uid}/{date}/{mediaType}/{filename}.{ext}";

    private FilePathUtil() {
    }

    /**
     * 设置自定义模板路径
     *
     * @param customTemplatePath 自定义模板路径
     */
    public static void setTemplatePath(String customTemplatePath) {
        if (StringUtils.isNotBlank(customTemplatePath)) {
            templatePath = customTemplatePath;
        }
    }

    public static String buildPathCommon(String filename) {
        return buildPathCommon(filename, true);
    }

    public static String buildPathCommon(String filename, Boolean filenameAutoGen) {
        return buildPathCommon(filename, filenameAutoGen, null, null, null, null);
    }

    public static String buildPathCommon(String filename, String mediaType) {
        return buildPathCommon(filename, true, null, null, mediaType, null);
    }

    /**
     * 构建文件路径（不含桶路径）
     *
     * @param filename           文件名
     * @param filenameAutoGen    自动生成文件名
     * @param uid                用户Id
     * @param time               时间
     * @param mediaType          媒体类型（images、docs、files 等）
     * @param filePermissionPath 文件权限路径
     * @return 文件路径
     */
    public static String buildPathCommon(String filename, Boolean filenameAutoGen, String uid, Date time, String mediaType, String filePermissionPath) {
        // 使用当前模板路径副本避免污染原始模板
        String path = templatePath;
        if (StringUtils.isNotBlank(filePermissionPath)) {
            path = "/" + filePermissionPath + path;
        }
        if (StringUtils.isNotBlank(uid)) {
            path = path.replace("{uid}", uid);
        } else {
            path = path.replace("{uid}", "0001");
        }
        path = path.replace("{date}", DateUtil.format(Objects.requireNonNullElseGet(time, Date::new), "yyyyMMdd"));
        if (StringUtils.isNotBlank(mediaType)) {
            path = path.replace("{mediaType}", mediaType);
        } else {
            path = path.replace("{mediaType}", "files");
        }
        if (Boolean.TRUE.equals(filenameAutoGen)) {
            path = path.replace("{filename}", IdUtil.fastSimpleUUID());
            path = path.replace("{ext}", FileUtil.extName(filename));
        } else {
            path = path.replace("{filename}.{ext}", filename);
        }
        return path;
    }
}

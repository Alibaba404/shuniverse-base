package cn.shuniverse.base.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by 蛮小满Sama at 2026-01-23 14:48
 *
 * @author 蛮小满Sama
 * @description
 */
public class FilePathUtil {
    private FilePathUtil() {
    }


    public static String buildFilePath(String filename, String formatter, String fileSourceCategory) {
        return buildFilePath(filename, null, formatter, fileSourceCategory);
    }

    public static String buildFilePath(String filename, String uid, String formatter, String fileSourceCategory) {
        return buildFilePath(filename, uid, null, formatter, fileSourceCategory);
    }

    public static String buildFilePath(String filename, String uid, Date time, String formatter, String fileSourceCategory) {
        if (null == time) {
            time = new Date();
        }
        if (StringUtils.isBlank(formatter)) {
            formatter = "yyyyMMdd";
        }
        // 获取文件后缀
        String extensionName = FileUtil.extName(filename);
        // 日期转文件夹
        String dateString = DateUtil.format(time, formatter);
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(fileSourceCategory)) {
            builder.append("/").append(fileSourceCategory);
        }
        if (StringUtils.isNotBlank(uid)) {
            builder.append("/").append(uid);
        }
        builder.append("/").append(dateString);
        builder.append("/").append(IdUtil.fastSimpleUUID()).append(".").append(extensionName);
        // 拼接存储路径
        return builder.toString();
    }
}

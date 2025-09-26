package cn.shuniverse.base.utils;

import cn.shuniverse.base.constants.DateConstants;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.zip.Deflater;

/**
 * Created by 蛮小满Sama at 2025-06-22 11:55
 *
 * @author 蛮小满Sama
 * @description 压缩工具类
 */
public class ApacheZipUtil {

    private ApacheZipUtil() {
    }
    /**
     * 压缩文件
     *
     * @param file     被压缩文件
     * @param basePath 基准路径
     * @param zos      压缩输出流
     * @throws IOException
     */
    public static void compress(File file, String basePath, ZipArchiveOutputStream zos) throws IOException {
        String relativePath = file.getName();
        if (StringUtils.isNotBlank(basePath)) {
            relativePath = getRelativePath(file, basePath);
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null || children.length == 0) {
                // 空目录也要保留，但 ZIP 标准要求目录名必须以 正斜杠 / 结尾，否则很多解压器不会识别为“目录”
                zos.putArchiveEntry(new ZipArchiveEntry(file, relativePath + "/"));
                zos.closeArchiveEntry();
            } else {
                for (File child : children) {
                    compress(child, basePath, zos);
                }
            }
        } else {
            try (InputStream fis = new BufferedInputStream(new FileInputStream(file))) {
                zos.putArchiveEntry(new ZipArchiveEntry(file, relativePath));
                IOUtils.copy(fis, zos);
                zos.closeArchiveEntry();
            }
        }
    }

    /**
     * 压缩目录或文件为 ZIP
     *
     * @param source 源文件或目录
     * @param zip    输出 zip 文件
     * @param level  压缩级别:Deflater
     *               NO_COMPRESSION         0 - 不压缩
     *               BEST_SPEED             1 - 快速压缩
     *               BEST_COMPRESSION       9 - 最大压缩率
     *               DEFAULT_COMPRESSION    -1 - 默认
     * @return 压缩文件的绝对路径
     * @throws IOException 压缩失败
     */
    public static String zip(File source, File zip, int level) throws IOException {
        if (source == null || !source.exists()) {
            throw new IllegalArgumentException("源文件不存在: " + source);
        }
        // 如果压缩文件已经存在，则重命名将要生产的压缩文件
        if (zip.exists()) {
            String timestamp = MeDateUtil.timestamp2DateString(Clock.systemUTC().millis(), DateConstants.DATETIME_FORMAT_SIMPLE);
            // 获取文件名（不含 .zip后缀）
            String baseName = zip.getName().replaceFirst("\\.zip$", "");
            // 构造新的文件名：如 image_20250622164501.zip
            zip = new File(zip.getParent(), baseName + "_" + timestamp + ".zip");
        }
        // 根据操作系统设置编码（Windows 推荐 GBK，其他系统使用 UTF-8）
        String encoding = Platform.isWindows() ? "GBK" : "UTF-8";
        try (OutputStream fos = new FileOutputStream(zip);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ZipArchiveOutputStream zos = new ZipArchiveOutputStream(bos)) {
            zos.setEncoding(encoding);
            zos.setUseZip64(Zip64Mode.AsNeeded);
            // 设置压缩等级
            zos.setLevel(level);
            compress(source, source.isFile() ? source.getParent() : source.getAbsolutePath(), zos);
        }
        return zip.getAbsolutePath();
    }

    /**
     * 压缩目录或文件为 ZIP
     *
     * @param source 源文件或目录
     * @param level  压缩级别
     * @throws IOException 压缩失败
     */
    public static String zip(File source, int level) throws IOException {
        // 构建输出 zip 文件路径：在 source 同级目录下
        return zip(source, new File(source.getParentFile(), source.getName() + ".zip"), level);
    }

    /**
     * 压缩目录或文件为 ZIP(快速压缩)
     *
     * @param source 源文件或目录
     * @throws IOException 压缩失败
     */
    public static String zip(File source) throws IOException {
        // 构建输出 zip 文件路径：在 source 同级目录下
        return zip(source, Deflater.BEST_SPEED);
    }

    /**
     * 压缩目录或文件为 ZIP(快速压缩)
     *
     * @param source  源文件或目录
     * @param zipName 压缩文件名
     * @throws IOException 压缩失败
     */
    public static String zip(File source, String zipName) throws IOException {
        File zip = new File(source.getParentFile().getAbsolutePath() + File.separator + zipName);
        // 构建输出 zip 文件路径：在 source 同级目录下
        return zip(source, zip, Deflater.BEST_SPEED);
    }

    /**
     * 获取真实路径替换为标准路径
     *
     * @param file     文件
     * @param basePath 基础路径
     * @return 返回替换的真实路径
     * @throws IllegalArgumentException 获取路径异常
     * @throws IOException              获取路径异常
     */
    public static String getRelativePath(File file, String basePath) throws IOException {
        // 解析符号链接
        Path base = Paths.get(basePath).toRealPath();
        // 规范化路径
        Path target = file.getCanonicalFile().toPath();
        if (!target.startsWith(base)) {
            throw new IllegalArgumentException("文件不在基准目录内: " + file);
        }
        return base.relativize(target).toString().replace('\\', '/');
    }

}

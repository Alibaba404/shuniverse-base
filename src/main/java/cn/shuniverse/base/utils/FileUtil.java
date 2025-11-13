package cn.shuniverse.base.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by 蛮小满Sama at 2025-06-19 21:55
 *
 * @author 蛮小满Sama
 * @description
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {
    /**
     * 从输入流中读取内容
     *
     * @param inputStream 输入流
     * @param charset     编码
     * @return 内容
     * @throws IOException 读取异常
     */
    public static String readString(InputStream inputStream, Charset charset) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream, charset);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();
        }
    }

    /**
     * 从输入流中按UTF8读取内容
     *
     * @param inputStream 输入流
     * @return 内容
     * @throws IOException 读取异常
     */
    public static String readUtf8String(InputStream inputStream) throws IOException {
        return readString(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * 从输入流按UTF8格式读取每行
     *
     * @param inputStream 输入流
     * @return 文件内容
     * @throws IOException 读取异常
     */
    public static List<String> readUtf8Lines(InputStream inputStream) throws IOException {
        return readLines(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * 从输入流按行读取文件
     *
     * @param inputStream 输入流
     * @param charset     编码
     * @return 文件内容
     * @throws IOException 读取异常
     */
    public static List<String> readLines(InputStream inputStream, Charset charset) throws IOException {
        List<String> lines = new ArrayList<>();
        try (InputStreamReader reader = new InputStreamReader(inputStream, charset);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        }
    }

    /**
     * 按行处理文件内容
     *
     * @param inputStream 输入流
     * @param charset     编码
     * @param lineHandler 行处理器
     * @throws IOException
     */
    public static void readLines(InputStream inputStream, Charset charset, Consumer<String> lineHandler) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineHandler.accept(line);
            }
        }
    }

    /**
     * 按行处理文件内容，编码为UTF-8
     *
     * @param inputStream 输入流
     * @param lineHandler 行处理器
     * @throws IOException
     */
    public static void readUtf8Lines(InputStream inputStream, Consumer<String> lineHandler) throws IOException {
        readLines(inputStream, StandardCharsets.UTF_8, lineHandler);
    }

    /**
     * 在jar包运行目录下创建目录
     *
     * @param path 目录路径
     */
    public static File mkRelativeDirectory(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        // 获取jar运行目录
        String absolutePath = new File("").getAbsolutePath();
        // 创建目录
        return mkdir(absolutePath + File.separator + path);
    }
}

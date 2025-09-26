package cn.shuniverse.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by 蛮小满Sama at 2025-06-18 13:53
 *
 * @author 蛮小满Sama
 * @description 目录工具类
 */

public class DirectoryUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryUtil.class);

    private static final int DEEP_MAX = 3;


    /**
     * 判断是否需要跳过的目录：
     * - 隐藏目录
     * - 符号链接
     * - Finder 替身（alias）
     */
    public static boolean isSkip(Path path) throws IOException {
        String name = path.getFileName().toString();
        // 隐藏目录
        if (name.startsWith(".")) {
            return true;
        }
        // 符号链接
        if (Files.isSymbolicLink(path)) {
            return true;
        }
        // Finder 替身（alias 文件）
        return isMacAlias(path);
    }

    /**
     * 使用 macOS `mdls` 命令判断是否为 Finder 替身（alias）
     */
    public static boolean isMacAlias(Path path) throws IOException {
        if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
            return false;
        }
        Process process = new ProcessBuilder("mdls", path.toString()).start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.lines()
                    .anyMatch(line ->
                            line.contains("kMDItemFSIsAlias = 1")
                                    || line.contains("kMDItemFSIsSymlink = 1")
                                    || line.contains("com.apple.alias-file")
                                    || line.contains("kMDItemKind                        = \"替身\"")
                    );
        }
    }

    public static List<Path> listDirectory(Path directory, boolean deep, int deepCount, boolean isOnlyDirectory, List<String> excludes) throws IOException {
        List<Path> result = new ArrayList<>();
        if (!Files.isDirectory(directory)) {
            return result;
        }
        if (deepCount >= DEEP_MAX) {
            return result;
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path path : directoryStream) {
                if ((!CollectionUtils.isEmpty(excludes) && excludes.contains(path.getFileName().toString()))
                        || isSkip(path)
                        || (isOnlyDirectory && Files.isRegularFile(path))
                ) {
                    continue;
                }
                result.add(path);
                if (deep && path.toFile().isDirectory()) {
                    // 递归处理子目录
                    result.addAll(listDirectory(path, true, deepCount + 1, isOnlyDirectory, excludes));
                }
            }
        }
        return result;
    }


    public static List<Path> listDirectory(Path dir) throws IOException {
        return listDirectory(dir, true);
    }

    public static List<Path> listDirectory(Path dir, boolean deep) throws IOException {
        return listDirectory(dir, deep, 0, true, Collections.emptyList());
    }

    public static List<Path> listDirectory(Path dir, boolean deep, List<String> excludes) throws IOException {
        return listDirectory(dir, deep, 0, true, excludes);
    }

    /**
     * 列出目录下的所有真实目录
     *
     * @param dir             目录
     * @param excludes        排除的目录
     * @param isOnlyDirectory 是否只返回目录
     * @return
     * @throws IOException
     */
    public static List<Path> listDirectory(Path dir, List<String> excludes, boolean isOnlyDirectory) throws IOException {
        return listDirectory(dir, false, 0, isOnlyDirectory, excludes);
    }

    /**
     * 列出目录下的所有真实目录(默认：不递归，仅提取目录)
     *
     * @param dir      目录
     * @param excludes 排除的目录
     * @return
     * @throws IOException
     */
    public static List<Path> listDirectory(Path dir, List<String> excludes) throws IOException {
        return listDirectory(dir, false, 0, true, excludes);
    }

    /**
     * 列出目录下的所有真实路径对象
     *
     * @param directory       目录(绝对路径)
     * @param isOnlyDirectory 是否只返回目录
     * @param excludes        排除的目录
     * @return
     * @throws IOException
     */
    public static List<Path> listPath(Path directory, boolean isOnlyDirectory, List<String> excludes) throws IOException {
        List<Path> result = new ArrayList<>();
        if (!Files.isDirectory(directory)) {
            return result;
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path path : directoryStream) {
                if ((!CollectionUtils.isEmpty(excludes) && excludes.contains(path.getFileName().toString()))
                        || isSkip(path)
                        || (isOnlyDirectory && Files.isRegularFile(path))
                ) {
                    continue;
                }
                result.add(path);
            }
        }
        return result;
    }

    /**
     * 列出目录下的所有真实路径对象
     *
     * @param directory 目录(绝对路径)
     * @return
     * @throws IOException
     */
    public static List<Path> listPath(Path directory) throws IOException {
        return listPath(directory, false, Collections.emptyList());
    }


    /**
     * 列出目录下的所有真实路径对象
     *
     * @param directory       目录(绝对路径)
     * @param isOnlyDirectory 是否只返回目录
     * @return
     * @throws IOException
     */
    public static List<Path> listPath(Path directory, boolean isOnlyDirectory) throws IOException {
        return listPath(directory, isOnlyDirectory, Collections.emptyList());
    }


    public static long getSize(String pathStr) throws IOException {
        Path path = Paths.get(pathStr);
        if (Files.isRegularFile(path)) {
            // 如果是文件:返回字节数
            return Files.size(path);
        } else if (Files.isDirectory(path)) {
            // 如果是目录:遍历所有文件计算文件大小(不递归搜索)
            try (Stream<Path> walkStream = Files.walk(path)) {
                return walkStream
                        .filter(p -> p.toFile().isFile())
                        .mapToLong(p -> p.toFile().length())
                        .sum();
            } catch (FileSystemException ex) {
                LOGGER.error("directory:“{}” Operation not permitted!", pathStr, ex);
                return 0L;
            }
        }
        throw new IOException("Path is neither file nor directory");
    }
}

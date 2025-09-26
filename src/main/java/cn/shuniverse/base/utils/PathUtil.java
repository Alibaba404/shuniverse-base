package cn.shuniverse.base.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by 蛮小满Sama at 2025-07-03 13:25
 *
 * @author 蛮小满Sama
 * @description 路径工具类
 */
public class PathUtil {
    /**
     * 获取程序运行目录（开发时返回模块目录，打包时返回 JAR 同级目录）
     */
    public static Path getJarRootPath(Class<?> baseClass) throws URISyntaxException {
        Path baseClassPath = Paths.get(baseClass.getProtectionDomain().getCodeSource().getLocation().toURI());
        // 如果是 JAR，返回 JAR 的父目录；否则是 IDE 运行，返回模块目录
        if (Files.isRegularFile(baseClassPath)) {
            // 运行时：.../app.jar -> 取其所在目录
            return baseClassPath.getParent();
        } else {
            // 开发时：.../target/classes 或 build/classes/... -> 往上找到模块目录
            return searchModuleRoot(baseClassPath);
        }
    }

    /**
     * 递归向上查找模块根目录（判断是否包含 pom.xml 或 build.gradle 等）
     */
    private static Path searchModuleRoot(Path baseClassPath) {
        Path path = baseClassPath;
        while (path != null && path.getParent() != null) {
            if (Files.exists(path.resolve("pom.xml"))
                    || Files.exists(path.resolve("build.gradle"))) {
                return path;
            }
            path = path.getParent();
        }
        throw new IllegalStateException("未能找到模块根目录（未发现 pom.xml 或 build.gradle）");
    }

    /**
     * 创建与jar同级目录的子目录允许创建多层
     *
     * @param baseClass     启动类
     * @param subPathString 子目录(例如:test/aaa/bbb)
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Path createSubPath(Class<?> baseClass, String subPathString) throws URISyntaxException, IOException {
        // 如果subPathString已存在,则直接返回
        Path subPath = Paths.get(subPathString);
        if (Files.exists(subPath)) {
            return subPath;
        }
        Path jarRootPath = getJarRootPath(baseClass);
        Path dir = jarRootPath.resolve(subPathString);
        Files.createDirectories(dir);
        return dir;
    }

    public static Path createSubPath(String homePathProject, String subPathString) throws IOException {
        // 如果subPathString已存在,则直接返回
        Path subPath = Paths.get(subPathString);
        if (Files.exists(subPath)) {
            return subPath;
        }
        Path root = Path.of(homePathProject);
        Path dir = root.resolve(subPathString);
        Files.createDirectories(dir);
        return dir;
    }
}

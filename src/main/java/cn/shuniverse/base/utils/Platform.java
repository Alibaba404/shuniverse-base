package cn.shuniverse.base.utils;

/**
 * Created by 蛮小满Sama at 2025-06-22 12:39
 *
 * @author 蛮小满Sama
 * @description
 */
public class Platform {
    private static final String WINDOWS = "Windows";
    private static final String MAC = "Mac";
    private static final String MAC_DARWIN = "Darwin";
    private static final String LINUX = "Linux";
    private static final String UNKNOWN = "Unknown";

    private Platform() {
    }

    public static String getPlatform() {
        String platform = System.getProperty("os.name");
        if (platform.startsWith(WINDOWS)) {
            return WINDOWS;
        } else if (platform.startsWith(MAC) || platform.startsWith(MAC_DARWIN)) {
            return MAC;
        } else if (platform.startsWith(LINUX)) {
            return LINUX;
        } else {
            return UNKNOWN;
        }
    }

    public static boolean isMac() {
        return MAC.equals(getPlatform());
    }

    public static boolean isWindows() {
        return WINDOWS.equals(getPlatform());
    }

}

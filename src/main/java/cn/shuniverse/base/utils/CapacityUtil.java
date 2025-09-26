package cn.shuniverse.base.utils;


import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 蛮小满Sama at 2025-06-15 22:44
 *
 * @author 蛮小满Sama
 * @description 容量工具类（支持字节单位转换/格式化/运算）
 */
public class CapacityUtil {
    private static final long KB = 1024L;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;
    private static final long TB = GB * 1024;
    private static final DecimalFormat DF = new DecimalFormat("#.##");

    // 禁止实例化
    private CapacityUtil() {
    }

    /**
     * 将字节数转换为可读性字符串（自动选择合适单位）
     *
     * @param bytes 字节数
     * @return 如 "1.23 MB" 的格式化字符串
     */
    public static String toReadable(long bytes) {
        if (bytes < KB) {
            return bytes + " B";
        } else if (bytes < MB) {
            return DF.format((double) bytes / KB) + " KB";
        } else if (bytes < GB) {
            return DF.format((double) bytes / MB) + " MB";
        } else if (bytes < TB) {
            return DF.format((double) bytes / GB) + " GB";
        } else {
            return DF.format((double) bytes / TB) + " TB";
        }
    }

    /**
     * KB 转字节
     */
    public static long kbToBytes(double kb) {
        return (long) (kb * KB);
    }

    /**
     * MB 转字节
     */
    public static long mbToBytes(double mb) {
        return (long) (mb * MB);
    }

    /**
     * GB 转字节
     */
    public static long gbToBytes(double gb) {
        return (long) (gb * GB);
    }

    /**
     * 字节转KB
     */
    public static double bytesToKb(long bytes) {
        return (double) bytes / KB;
    }

    /**
     * 字节转MB
     */
    public static double bytesToMb(long bytes) {
        return (double) bytes / MB;
    }

    /**
     * 字节转GB
     */
    public static double bytesToGb(long bytes) {
        return (double) bytes / GB;
    }

    /**
     * 安全加法（防止long溢出）
     *
     * @return 若溢出则返回Long.MAX_VALUE
     */
    public static long safeAdd(long a, long b) {
        try {
            return Math.addExact(a, b);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * 计算百分比使用率
     *
     * @param used  已用容量
     * @param total 总容量
     * @return 如 75.5（表示75.5%）
     */
    public static double calculateUsagePercent(long used, long total) {
        if (total <= 0) {
            return 0;
        }
        return (used * 100.0) / total;
    }

    /**
     * 获取容量大小
     *
     * @param size 容量大小
     * @param unit 单位
     * @return 容量大小
     */
    public static long takeSize(int size, String unit) {
        return switch (unit) {
            case "KB" -> size * 1024L;
            case "MB" -> (long) size * 1024 * 1024;
            case "GB" -> (long) size * 1024 * 1024 * 1024;
            case "TB" -> (long) size * 1024 * 1024 * 1024 * 1024;
            default -> 0;
        };
    }

    private static final Pattern SIZE_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(B|KB|MB|GB|TB|KIB|MIB|GIB|TIB)?", Pattern.CASE_INSENSITIVE);

    /**
     * 获取字节大小
     * 20M,20MB,20 MB
     *
     * @param sizeStr 容量(带单位)
     * @param strict  是否严格模式
     *                ✅ 在“宽松模式(strict=false)”下，忽略 MB 与 MiB 的差异（都按 1024*1024 处理）
     *                ✅ 在“严格模式(strict=true)”下，严格区分 SI 与二进制标准
     * @return 字节数
     */
    public static long getBytes(String sizeStr, boolean strict) {
        Matcher matcher = getMatcher(sizeStr, strict);
        double value = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2);
        if (unit == null) {
            unit = "B";
        }
        return switch (unit) {
            case "B" -> (long) value;
            case "KB" -> (long) (value * (strict ? 1000L : 1024L));
            case "MB" -> (long) (value * (strict ? 1000_000L : 1024L * 1024));
            case "GB" -> (long) (value * (strict ? 1000_000_000L : 1024L * 1024 * 1024));
            case "TB" -> (long) (value * (strict ? 1_000_000_000_000L : 1024L * 1024 * 1024 * 1024));
            case "KIB" -> (long) (value * 1024L);
            case "MIB" -> (long) (value * 1024L * 1024);
            case "GIB" -> (long) (value * 1024L * 1024 * 1024);
            case "TIB" -> (long) (value * 1024L * 1024 * 1024 * 1024);
            default -> throw new IllegalArgumentException("Unsupported unit: " + unit);
        };
    }

    private static Matcher getMatcher(String sizeStr, boolean strict) {
        if (sizeStr == null || sizeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Size string cannot be null or empty.");
        }
        // 标准化
        String normalized = sizeStr.trim().toUpperCase(Locale.ROOT);
        // 宽松模式自动补全单位
        if (!strict) {
            normalized = normalized
                    .replaceAll("(?<=\\d)\\s*K$", "KB")
                    .replaceAll("(?<=\\d)\\s*M$", "MB")
                    .replaceAll("(?<=\\d)\\s*G$", "GB")
                    .replaceAll("(?<=\\d)\\s*T$", "TB");
        }
        Matcher matcher = SIZE_PATTERN.matcher(normalized);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid size format: " + sizeStr);
        }
        return matcher;
    }

    /**
     * 获取字节大小(默认宽松模式)
     *
     * @param sizeStr 容量(带单位)
     * @return 字节数
     */
    public static long getBytes(String sizeStr) {
        return getBytes(sizeStr, false);
    }
}

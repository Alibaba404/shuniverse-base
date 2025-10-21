package cn.shuniverse.base.utils;

import cn.hutool.core.date.DateUtil;
import cn.shuniverse.base.constants.DateConstants;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Locale;

/**
 * Created by 蛮小满Sama at 2025-06-20 03:10
 *
 * @author 蛮小满Sama
 * @description 时间工具类
 */
@Slf4j
public class DatetimeUtil extends DateUtil {

    private static final String[] DATE_PATTERNS = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyy年M月d日",
            "yyyy年M月d日 HH:mm:ss",
            "yyyy年M月d日 HH:mm",
            "dd-MM-yyyy",
            // 英文格式
            "MMM dd, yyyy HH:mm",
    };

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param timestamp 时间戳
     * @return 时间戳对应的日期字符串
     */
    public static String timestamp2DateString(long timestamp) {
        return format(date(timestamp), DateConstants.DATETIME_FORMAT);
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param timestamp 时间戳
     * @param format    格式
     * @return 时间戳对应的日期字符串
     */
    public static String timestamp2DateString(long timestamp, String format) {
        return format(date(timestamp), format);
    }

    /**
     * 获取LocalDateTime
     *
     * @param timeStr 时间字符串
     * @return
     */
    public static LocalDateTime getLocalDateTime(String timeStr) {
        for (String pattern : DATE_PATTERNS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withLocale(Locale.CHINA);
                TemporalAccessor parsed = formatter.parse(timeStr);
                LocalDate date;
                if (parsed.query(TemporalQueries.localDate()) != null) {
                    date = parsed.query(TemporalQueries.localDate());
                } else {
                    // 回退到LocalDateTime并提取日期
                    date = LocalDateTime.from(parsed).toLocalDate();
                }
                return LocalDateTime.of(date, LocalTime.MIDNIGHT);
            } catch (Exception e) {
                log.warn("无法解析时间字符串: {}", timeStr, e);
            }
        }
        throw new IllegalArgumentException("无法解析时间字符串: " + timeStr);
    }

    /**
     * <pre>
     *  获取两个时间之间的天数
     *      天数 < 0 表示timeStr在target之后
     *      天数 > 0 表示timeStr在target之前
     * </pre>
     *
     * @param timeStr 时间字符串
     * @param target  目标时间字符串
     * @return 天数
     */
    public static long getDaysBetweenFlexible(String timeStr, String target) {
        LocalDateTime targetLocalDate = LocalDateTime.now();
        if (target != null) {
            targetLocalDate = getLocalDateTime(target);
        }
        return ChronoUnit.DAYS.between(getLocalDateTime(timeStr), targetLocalDate);
    }
}

package cn.shuniverse.base.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.shuniverse.base.constants.DateConstants;
import com.nlf.calendar.Lunar;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 蛮小满Sama at 2025-06-20 03:10
 *
 * @author 蛮小满Sama
 * @description 时间工具类
 */
@Slf4j
public class DatetimeUtil extends DateUtil {
    // 3. 定义0基月份到Calendar常量的映射数组
    private static final int[] CALENDAR_MONTH_CONSTANTS = {
            Calendar.JANUARY,
            Calendar.FEBRUARY,
            Calendar.MARCH,
            Calendar.APRIL,
            Calendar.MAY,
            Calendar.JUNE,
            Calendar.JULY,
            Calendar.AUGUST,
            Calendar.SEPTEMBER,
            Calendar.OCTOBER,
            Calendar.NOVEMBER,
            Calendar.DECEMBER
    };

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

    /**
     * 计算生日倒计时天数
     *
     * @param month 农历月份
     * @param day   农历日期
     * @return 天数
     */
    public static String birthdayCountLunar(int month, int day) {
        // 1. 获取当前公历日期
        DateTime today = DateUtil.date();
        // 2. 获取当前年份
        int yearCur = today.year();
        // 2. 将农历生日（月/日）转换为当前年的公历日期
        Lunar birthdayLunar = new Lunar(yearCur, month, day);
        DateTime birthdaySolarDate = DateUtil.parseDate(birthdayLunar.getSolar().toString());
        // 3. 如果今年的生日已过，计算明年的
        if (birthdaySolarDate.isBefore(today)) {
            Lunar birthdayNextYearLunar = new Lunar(yearCur + 1, month, day);
            birthdaySolarDate = DateUtil.parseDate(birthdayNextYearLunar.getSolar().toString());
        }
        // 4. 计算天数差
        return String.valueOf(DateUtil.betweenDay(today, birthdaySolarDate, true));
    }


    /**
     * 计算生日倒计时天数（传入农历）
     *
     * @param birthday 出生年月
     * @return 天数
     */
    public static String birthdayCountLunar(String birthday) {
        if (StringUtils.isBlank(birthday)) {
            return "0";
        }
        DateTime birthDateObj = DateUtil.parseDate(birthday);
        return birthdayCountLunar(DateUtil.month(birthDateObj) + 1, DateUtil.dayOfMonth(birthDateObj));
    }

    /**
     * 获取生日倒计时天数
     *
     * @param birthday 出生年月日（公历）
     * @return 天数
     */
    @SuppressWarnings("all")
    public static String birthdayCount(String birthday) {
        if (birthday == null) {
            return "0";
        }

        DateTime birthdayObj = DateUtil.parseDate(birthday);
        int month = DateUtil.month(birthdayObj);
        int day = DateUtil.dayOfMonth(birthdayObj);

        // 今天
        DateTime today = DateUtil.date();
        int yearCur = today.year();

        // 1. 获取当前公历日期
        Calendar birthdayCal = Calendar.getInstance(Locale.CHINA);
        birthdayCal.set(yearCur, month, day);
        // 2. 将农历生日（月/日）转换为当前年的公历日期
        DateTime birthdaySolarDate = DateUtil.date(birthdayCal);
        // 3. 如果今年的生日已过，计算明年的
        if (birthdaySolarDate.before(today)) {
            birthdayCal.clear();
            birthdayCal.set(yearCur + 1, month, day);
            birthdaySolarDate = DateUtil.date(birthdayCal);
        }
        // 4. 计算天数差
        return String.valueOf(DateUtil.betweenDay(today, birthdaySolarDate, true));
    }
}

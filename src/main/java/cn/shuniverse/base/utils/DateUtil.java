package cn.shuniverse.base.utils;

import cn.shuniverse.base.constants.DateConstants;

/**
 * Created by 蛮小满Sama at 2025-06-20 03:10
 *
 * @author 蛮小满Sama
 * @description 时间工具类
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {
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
}

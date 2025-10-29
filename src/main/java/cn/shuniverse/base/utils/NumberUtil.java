package cn.shuniverse.base.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by 蛮小满Sama at 2025-10-21 09:53
 *
 * @author 蛮小满Sama
 * @description 数字工具类
 */
@Slf4j
public class NumberUtil {

    /**
     * 将数字转换为中文数字
     *
     * @param value 数字
     * @return
     */
    public static String toChineseNumber(long value) {
        if (value == 0) {
            return "零";
        }
        String[] units = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        String[] digits = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        StringBuilder builder = new StringBuilder();
        String numStr = String.valueOf(value);
        int len = numStr.length();
        for (int i = 0; i < len; i++) {
            int digit = numStr.charAt(i) - '0';
            int pos = len - i - 1;
            if (digit != 0) {
                builder.append(digits[digit]).append(units[pos]);
            } else {
                // 避免连续多个零
                if (!builder.isEmpty() && builder.charAt(builder.length() - 1) != '零') {
                    builder.append("零");
                }
            }
        }
        // 特殊处理：去除尾部“零”
        String result = builder.toString().replaceAll("零+$", "");
        // 处理如“一十X” => “十X”
        if (result.startsWith("一十")) {
            result = result.substring(1);
        }
        return result;
    }


    /**
     * 将数字转换为指定长度的数字
     *
     * @param value  数字
     * @param length 长度
     * @param digit  填充的数字
     * @return
     */
    public static String toCompleteNumber(int value, int length, String digit) {
        String val = String.valueOf(value);
        if (val.length() > length) {
            return val;
        }
        if (value < 0) {
            return val;
        }
        if (digit == null || digit.isEmpty()) {
            digit = "0";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(digit.repeat(length));
        builder.replace(builder.length() - val.length(), builder.length(), val);
        return builder.toString();
    }

    public static String toCompleteNumber(int value, int length) {
        return toCompleteNumber(value, length, "0");
    }

    public static String toCompleteNumber4Bit(int value) {
        return toCompleteNumber(value, 4, "0");
    }
}

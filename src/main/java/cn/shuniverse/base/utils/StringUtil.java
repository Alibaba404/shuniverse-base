package cn.shuniverse.base.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by 蛮小满Sama at 2025-06-30 15:41
 *
 * @author 蛮小满Sama
 * @description
 */
public class StringUtil extends StringUtils {
    /**
     * 编码
     *
     * @param character 待编码字符
     * @return 编码后的字符
     */
    public static String encoding(String character) {
        return encoding(character, StandardCharsets.UTF_8);
    }

    public static String encoding(String character, Charset charset) {
        return URLEncoder.encode(character, charset);
    }

    public static String encoding(String character, String charset) throws UnsupportedEncodingException {
        return URLEncoder.encode(character, charset);
    }
}

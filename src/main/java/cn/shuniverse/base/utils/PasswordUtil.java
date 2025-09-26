package cn.shuniverse.base.utils;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.shuniverse.base.constants.SystemConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by 蛮小满Sama at 2024-11-23 12:34
 *
 * @author 蛮小满Sama
 * @description 密码工具
 */
public class PasswordUtil {
    // 常见弱密码列表
    private static final List<String> COMMON_WEAK_PASSWORDS = Arrays.asList("123456", "123456789", "password",
            "qwerty", "abc123", "111111", "123123", "admin", "letmein", "welcome", "monkey", "football", "iloveyou",
            "sunshine", "000000");
    // 定义密码规则的正则表达式
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{6,16}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);


    /**
     * 生成盐值
     *
     * @return 盐值
     */
    public static String genSalt() {
        return genSalt(SystemConstants.PASSWORD_KEY);
    }

    /**
     * 生成盐值
     *
     * @param salt 指定的盐值
     * @return 盐值
     */
    public static String genSalt(String salt) {
        if (StringUtils.isBlank(salt)) {
            salt = SystemConstants.PASSWORD_KEY;
        }
        return SecureUtil.aes().encryptHex(salt);
    }


    /**
     * 加密密码
     *
     * @param text 密码
     * @return 加密后的密码
     */
    public static String encrypt(String text) {
        //提高计算成本（范围 4~31，建议 >=10,推荐:12）
        return BCrypt.hashpw(text, BCrypt.gensalt(12));
    }

    /**
     * 验证密码是否匹配
     *
     * @param text          明文密码
     * @param encryptedText 已加密的密码
     * @return 密码是否匹配
     */
    public static boolean match(String text, String encryptedText) {
        return BCrypt.checkpw(text, encryptedText);
    }

    /**
     * 检测弱密码
     *
     * @param password 待检测的密码
     * @return 是否为弱密码
     */
    public static boolean isWeakPassword(String password) {
        // 空密码视为弱密码
        if (password == null || password.trim().isEmpty()) {
            return true;
        }

        // 1. 检测是否在常见弱密码列表中
        if (COMMON_WEAK_PASSWORDS.contains(password.toLowerCase())) {
            return true;
        }

        // 2. 检测是否为重复字符（如 "aaaaaa" 或 "111111"）
        if (isRepeatingCharacters(password)) {
            return true;
        }

        // 3. 检测是否为连续字符或数字（如 "abcde" 或 "12345"）
        if (isSequential(password)) {
            return true;
        }

        // 4. 检测是否包含键盘模式
        return containsKeyboardPattern(password.toLowerCase());
    }

    /**
     * 检测是否为重复字符
     *
     * @param password 待检测的密码
     * @return 是否为重复字符
     */
    private static boolean isRepeatingCharacters(String password) {
        return password.chars().distinct().count() == 1;
    }

    /**
     * 检测是否为连续字符
     *
     * @param password 待检测的密码
     * @return 是否为连续字符
     */
    private static boolean isSequential(String password) {
        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i + 1] != chars[i] + 1) {
                // 不是连续字符
                return false;
            }
        }
        return true;
    }

    /**
     * 检测是否包含键盘模式
     *
     * @param password 待检测的密码
     * @return 是否包含键盘模式
     */
    private static boolean containsKeyboardPattern(String password) {
        String[] keyboardPatterns = {"qwerty", "asdfgh", "zxcvbn", "123456", "098765"};
        for (String pattern : keyboardPatterns) {
            if (password.contains(pattern)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 校验密码是否符合规则
     *
     * @param password 待校验的密码
     * @return 是否符合规则
     */
    public static boolean validatePassword(String password) {
        // 密码不能为空
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }


    /**
     * 综合校验
     * <pre>
     *  1.长度为 6-16 位。
     *  2.至少包含一个大写字母、一个小写字母、一个数字和一个特殊字符。
     *  3.不允许空格。
     * </pre>
     *
     * @param password 待校验的密码
     * @return 是否符合综合校验
     */
    public static boolean validatePasswordWithWeakCheck(String password) {
        return validatePassword(password) && !isWeakPassword(password);
    }


}

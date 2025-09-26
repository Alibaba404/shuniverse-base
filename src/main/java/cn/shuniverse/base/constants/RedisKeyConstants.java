package cn.shuniverse.base.constants;

/**
 * Created by 蛮小满Sama at 2025-06-23 14:28
 *
 * @author 蛮小满Sama
 * @description redis key常量
 */
public class RedisKeyConstants {

    private RedisKeyConstants() {
    }

    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_CODE_PREFIX = "captcha:code:";

    /**
     * token前缀
     */
    public static final String TOKEN_PREFIX = "token:";

    /**
     * 令牌过期时间,单位秒(默认6小时过期)
     */
    public static final long DEFAULT_EXPIRE_TIME = 21600L;
}

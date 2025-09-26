package cn.shuniverse.base.constants;

import java.time.Duration;

/**
 * Created by 蛮小满Sama at 2025-05-19 15:18
 *
 * @author 蛮小满Sama
 * @description 系统常量
 */
public abstract class SystemConstants {

    protected SystemConstants() {
    }

    // 请求头携带 Token 请勿更改
    public static final String TOKEN_NAME = "satoken";
    // 请求头携带 Token 前缀 请勿更改
    public static final String TOKEN_PREFIX = "Bearer";
    // 密码加密密钥
    public static final String PASSWORD_KEY = "shuniverse2025.6";
    // 请求头携带 前端公钥 Key 请勿更改
    public static final String AUTHORITY_KEY = "Authorization";
    // sm密钥存储 public key
    public static final String PUBLIC_KEY = "public";
    // sm密钥存储 private key
    public static final String PRIVATE_KEY = "private";
    // 敏感数据替换符
    public static final String SENSITIVE_REPLACE = "[🙈非礼勿视🙈],[🙊非礼勿言🙊]";
    // 限流key
    public static final String RATE_LIMIT_KEY = "rate_limit:";
    // 默认过期时间
    public static final Duration DEFAULT_DURATION = Duration.ofDays(3L);
    // 游客角色ID
    public static final String VISITOR_ROLE_ID = "600000";
    // 管理员角色ID
    public static final String ADMIN_ROLE_ID = "100001";
    // 状态码-0-禁用
    public static final int STATUS_DISABLE = 0;
    // 状态码-1-正常
    public static final int STATUS_NORMAL = 1;
    // 数据是否删除-0-未删除
    public static final int DATA_DELETED_NO = 0;
    // 数据是否删除-1-已删除
    public static final int DATA_DELETED_YES = 1;

}

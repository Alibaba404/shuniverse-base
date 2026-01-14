package cn.shuniverse.base.utils;

import cn.hutool.core.date.SystemClock;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by 蛮小满Sama at 2025-06-19 16:23
 *
 * @author 蛮小满Sama
 * @description
 */
@Slf4j
@Component
public class JwtTokenUtil {
    // 服务端密钥（配置在application.yml，长度≥256位）
    @Value("${jwt.secret-key}")
    private String jwtSecret;
    // Access Token 过期时间（15分钟，单位：毫秒）
    private static final long ACCESS_TOKEN_EXPIRE = 15 * 60 * 1000L;
    // Refresh Token 过期时间（30天，单位：毫秒）
    private static final long REFRESH_TOKEN_EXPIRE = 30 * 24 * 60 * 60 * 1000L;
    // Token 黑名单前缀
    private static final String TOKEN_BLACKLIST_PREFIX = "jwt:blacklist:";

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * 生成 Access Token
     *
     * @param uid 用户ID
     */
    public String generateAccessToken(String uid) {
        return generateAccessToken(uid, null);
    }

    /**
     * 生成 Access Token
     *
     * @param uid    用户ID
     * @param claims 额外信息
     */
    public String generateAccessToken(String uid, Map<String, Object> claims) {
        return generateAccessToken(uid, claims, null);
    }

    public String generateAccessToken(String uid, Map<String, Object> claims, Long expire) {
        return tokenGet(uid, claims, expire);
    }

    /**
     * 生成 Refresh Token
     *
     * @return 64位随机字符串
     */
    public String generateRefreshToken() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成 JWT 通用方法
     *
     * @param subject 主题
     * @param claims  自定义信息
     * @param expire  过期时间（毫秒）
     */
    private String tokenGet(String subject, Map<String, Object> claims, Long expire) {
        if (expire == null) {
            expire = ACCESS_TOKEN_EXPIRE;
        }
        long now = SystemClock.now();
        if (Objects.isNull(claims)) {
            claims = new HashMap<>();
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expire))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从Access Token中获取用户ID
     *
     * @param token Access Token
     * @return 用户ID
     */
    public String getUid(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    /**
     * 主动让 Token 失效（加入黑名单）
     *
     * @param token         要失效的 Token（Access/Refresh Token）
     * @param expireSeconds 黑名单过期时间（建议等于 Token 剩余有效期）
     */
    public void invalidateToken(String token, long expireSeconds) {
        if (StringUtils.isBlank(token)) {
            return;
        }
        // 生成唯一key（用Token的MD5，避免key过长）
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + DigestUtils.md5DigestAsHex(token.getBytes());
        // 将Token加入黑名单，过期时间设为Token剩余有效期（避免黑名单无限膨胀）
        RedisUtil.set(blacklistKey, "invalid", expireSeconds, TimeUnit.SECONDS);
        log.info("TOKEN 已加入黑名单，失效时间：{}秒", expireSeconds);
    }

    /**
     * 检查 Token 是否在黑名单中
     *
     * @param token 待校验的 Token
     * @return true=已失效，false=未失效
     */
    public boolean isBlacklist(String token) {
        if (StringUtils.isBlank(token)) {
            return true;
        }
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + DigestUtils.md5DigestAsHex(token.getBytes());
        return Boolean.TRUE.equals(RedisUtil.hasKey(blacklistKey));
    }

    /**
     * 完善的 Token 校验（黑名单 + 签名 + 过期时间）
     *
     * @param token Access Token
     * @return true=有效，false=无效/已失效/已过期
     */
    public boolean validateToken(String token) {
        // 1. 先检查是否在黑名单（主动失效）
        if (isBlacklist(token)) {
            log.warn("Access Token 已被主动失效（黑名单）");
            return false;
        }
        // 2. 再校验签名和过期时间
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Access Token 已过期: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            log.error("Access Token 无效（篡改/格式错误/签名错误）: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Access Token 校验异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取 Token 剩余有效期（秒）
     *
     * @param token Access Token
     * @return 剩余秒数，-1=Token无效
     */
    public long getTokenRemainingExt(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            long expireTime = claims.getExpiration().getTime();
            long now = SystemClock.now();
            long remaining = (expireTime - now) / 1000;
            // 避免负数
            return Math.max(remaining, 0);
        } catch (Exception e) {
            log.error("获取 Token 剩余时间失败: {}", e.getMessage());
            return -1;
        }
    }
}

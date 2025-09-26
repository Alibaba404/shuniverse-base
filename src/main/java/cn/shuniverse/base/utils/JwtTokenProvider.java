package cn.shuniverse.base.utils;

import cn.hutool.core.date.SystemClock;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Created by 蛮小满Sama at 2025-06-19 16:23
 *
 * @author 蛮小满Sama
 * @description
 */
@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;
    /**
     * 访问令牌有效期(单位：秒)
     */
    @Value("${security.jwt.expire-access}")
    private long accessTokenExpire;
    /**
     * 刷新令牌有效期(单位：秒)
     */
    @Value("${security.jwt.expire-refresh}")
    private long refreshTokenExpire;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 生成 Access Token
     */
    public String getAccessToken(String subject, Map<String, Object> claims) {
        return tokenGet(subject, claims, accessTokenExpire * 1000);
    }

    /**
     * 生成 Refresh Token
     */
    public String getRefreshToken(String subject) {
        return tokenGet(subject, null, refreshTokenExpire * 1000);
    }

    /**
     * 生成 JWT 通用方法
     */
    private String tokenGet(String subject, Map<String, Object> claims, long expire) {
        long now = SystemClock.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expire))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从 Token 中解析出用户名
     */
    public String getSubject(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * 解析 Claims
     */
    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}

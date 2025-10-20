package cn.shuniverse.base.core;

import cn.shuniverse.base.constants.SystemConstants;
import cn.shuniverse.base.utils.RedisUtil;
import cn.shuniverse.base.utils.SMEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by 蛮小满Sama at 2025-10-20 20:40
 *
 * @author 蛮小满Sama
 * @description SM2密钥缓存管理器
 */
@Slf4j
@Component
public class SM2KeyCacheManager {

    // 缓存有效期 1 天
    private static final Duration OVERDUE = Duration.ofDays(1);
    // Redis Key
    private static final String PUBLIC_KEY = "sm2:public";
    private static final String PRIVATE_KEY = "sm2:private";
    // 默认公钥
    private static final String PUBLIC_SECRET =
            "04737ec6c021f38f2d060ecae862f78cf3ebcba0b2123a71b9b8818b0a0ca3eb0576047b60f2c15ad49656a18ba5520b5fdbacab270b50e20a7f0ed3a198b755b4";
    // 默认私钥
    private static final String PRIVATE_SECRET = "5417a0d71eb89b3be18e41db4150474413d6d39cd10969edb4f44660423a9388";
    // 本地缓存
    private final AtomicReference<Map<String, String>> localCache = new AtomicReference<>(
            Map.of(SystemConstants.PUBLIC_KEY, PUBLIC_SECRET, SystemConstants.PRIVATE_KEY, PRIVATE_SECRET));

    public String getKey(String cacheKey, String systemKey, String defaultValue) {
        String value = RedisUtil.get(cacheKey, String.class);
        if (value == null) {
            Map<String, String> map = SMEncryptUtil.getSm2Key();
            if (ObjectUtils.isEmpty(map)) {
                // 使用本地缓存兜底
                log.warn("SM2密钥生成失败，使用本地缓存");
                return localCache.get().getOrDefault(systemKey, defaultValue);
            }
            set(map);
            return map.get(systemKey);
        }
        return value;
    }


    /**
     * 获取公钥并缓存到Redis
     *
     * @return
     */
    public String getPublicKey() {
        return getKey(PUBLIC_KEY, SystemConstants.PUBLIC_KEY, PUBLIC_SECRET);
    }

    /**
     * 获取私钥并缓存到Redis
     *
     * @return
     */
    public String getPrivateKey() {
        return getKey(PRIVATE_KEY, SystemConstants.PRIVATE_KEY, PRIVATE_SECRET);
    }


    @Async
    public void set(Map<String, String> map) {
        try {
            RedisUtil.set(PUBLIC_KEY, map.get(SystemConstants.PUBLIC_KEY), OVERDUE);
            RedisUtil.set(PRIVATE_KEY, map.get(SystemConstants.PRIVATE_KEY), OVERDUE);
        } catch (Exception e) {
            log.error("写入Redis失败: {}", e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        Map<String, String> sm2Key = SMEncryptUtil.getSm2Key();
        log.info("sm2PrivateKey: {}", sm2Key.get("private"));
        log.info("sm2PublicKey: {}", sm2Key.get("public"));
    }
}

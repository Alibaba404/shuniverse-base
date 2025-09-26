package cn.shuniverse.base.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.shuniverse.base.constants.RedisKeyConstants;
import cn.shuniverse.base.core.exception.BisException;
import cn.shuniverse.base.core.resp.RCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by 蛮小满Sama at 2025-06-23 14:36
 *
 * @author 蛮小满Sama
 * @description
 */
@Slf4j
@Component
public class RedisUtil implements InitializingBean {

    private final RedisTemplate<String, Object> injectedTemplate;
    private static RedisTemplate<String, Object> template;

    @Autowired
    RedisUtil(@Qualifier("redis") RedisTemplate<String, Object> injectedTemplate) {
        this.injectedTemplate = injectedTemplate;
    }

    public static Set<String> scan(String key, int scanCount) {
        return template.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            // 使用 SCAN 替代 KEYS（非阻塞）
            Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions()
                            // 匹配模式，如 "resources_label:*"
                            .match(key)
                            // 每次扫描的键数量（避免单次过多）
                            .count(scanCount)
                            .build()
            );
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
            }
            return keys;
        });
    }

    public static Set<String> scan(String key) {
        return scan(key, 100);
    }

    @Override
    public void afterPropertiesSet() {
        template = injectedTemplate;
    }

    /**
     * 设置 key-value
     *
     * @param key   键
     * @param value 值
     */
    public static void set(String key, Object value) {
        template.opsForValue().set(key, value);
    }

    /**
     * 设置 key-value 并指定过期时间（秒）
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     */
    public static void set(String key, Object value, long timeout) {
        set(key, value, timeout, TimeUnit.SECONDS);
    }

    public static void set(String key, Object value, Duration timeout) {
        template.opsForValue().set(key, value, timeout);
    }

    /**
     * 设置 key-value 并指定过期时间（秒）
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     */
    public static void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        template.opsForValue().set(key, value, timeout, timeUnit);
    }

    // 获取 key 的值
    public static Object get(String key) {
        return template.opsForValue().get(key);
    }

    // 编译器警告已被抑制
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> clazz) {
        Object value = template.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        throw new IllegalStateException("Redis中 key=" + key + " 的值不能转换为类型 " + clazz.getName());
    }

    // 删除 key
    public static void delete(String key) {
        template.delete(key);
    }

    public static void delete(Collection<String> keys) {
        if (CollUtil.isNotEmpty(keys)) {
            template.delete(keys);
        }
    }

    // 检查 key 是否存在
    public static boolean hasKey(String key) {
        return Boolean.TRUE.equals(template.hasKey(key));
    }

    // 设置 key 的过期时间（秒）
    public static boolean expire(String key, long timeout) {
        return Boolean.TRUE.equals(template.expire(key, timeout, TimeUnit.SECONDS));
    }

    public static void expire(String key, Duration timeout) {
        template.expire(key, timeout);
    }

    // 获取所有匹配的 key
    public static Set<String> keys(String pattern) {
        Set<String> keys = template.keys(pattern);
        if (CollUtil.isEmpty(keys)) {
            return Collections.emptySet();
        }
        return keys.stream().map(Object::toString).collect(Collectors.toSet());
    }

    // 自增 key 的值
    public static Long increment(String key, long delta) {
        return template.opsForValue().increment(key, delta);
    }

    // 自减 key 的值
    public static Long decrement(String key, long delta) {
        return template.opsForValue().decrement(key, delta);
    }


    /**
     * 添加元素到列表的末尾
     *
     * @param key   key
     * @param value 值
     * @param <V>   值类型
     */
    public static <V> void rightPushAll(String key, Collection<V> value) {
        if (value != null && !value.isEmpty()) {
            template.opsForList().rightPushAll(key, value.toArray());
        }
    }

    /**
     * 验证码校验
     *
     * @param key  验证码key
     * @param code 验证码
     */
    public static void verifyCaptcha(String key, String code) {
        Object captchaCodeRedis = RedisUtil.get(RedisKeyConstants.CAPTCHA_CODE_PREFIX + key);
        if (null == captchaCodeRedis) {
            throw BisException.me(RCode.USER_CAPTCHA_EXPIRED);
        }
        if (!StringUtils.equalsIgnoreCase(code, captchaCodeRedis.toString())) {
            throw BisException.me(RCode.USER_CAPTCHA_NOT_MATCHED);
        }
    }

    public static List<Object> range(String key, long start, long end) {
        return template.opsForList().range(key, start, end);
    }

    /**
     * 构建模型
     *
     * @param models Redis缓存中的模型列表
     * @param clazz  需要转为的模型类
     * @param <T>
     * @return
     */
    public static <T> List<T> buildModels(List<Object> models, Class<T> clazz) {
        if (CollUtil.isNotEmpty(models)) {
            return models.stream()
                    .map(o -> {
                        try {
                            return JSONUtil.toBean(JSONUtil.toJsonStr(o), clazz);
                        } catch (Exception e) {
                            log.warn("JSON转换失败: {}", o, e);
                            throw BisException.me(RCode.FAILED.getCode(), e.getLocalizedMessage());
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        }
        return Collections.emptyList();
    }

    /**
     * 获取列表的长度
     *
     * @param key 列表的key
     * @return 列表的长度
     */
    public static long total(String key) {
        Long total = template.opsForList().size(key);
        return total == null ? 0L : total;
    }

    public static boolean hasKey(String key, String hashKey) {
        return template.opsForHash().hasKey(key, hashKey);
    }

    public static Object getHash(String key, String hashKey) {
        return template.opsForHash().get(key, hashKey);
    }

    public static <T> Object getHash(String key, String hashKey, Class<T> tClass) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(getHash(key, hashKey)), tClass);
    }

    public static void setHash(String key, String hashKey, Object value) {
        template.opsForHash().put(key, hashKey, value);
    }

    public static Long execute(RedisScript<Long> limitScript, List<String> keys, int count, int time) {
        return template.execute(limitScript, keys, count, time);
    }
}

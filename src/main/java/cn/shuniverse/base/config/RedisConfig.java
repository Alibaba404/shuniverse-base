package cn.shuniverse.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by 蛮小满Sama at 2025-06-23 14:47
 *
 * @author 蛮小满Sama
 * @description
 */
@Configuration
public class RedisConfig {
    @Bean("redis")
    public RedisTemplate<String, String> getRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(limitScriptText());
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /**
     * 限流脚本
     */
    private String limitScriptText() {
        return """
                local key = KEYS[1];
                local count = tonumber(ARGV[1]);
                local time = tonumber(ARGV[2]);
                local current = redis.call('get', key);
                if current and tonumber(current) > count then
                    return tonumber(current);
                end;
                current = redis.call('incr', key);
                if current and tonumber(current) == 1 then
                    redis.call('expire', key, time);
                end;
                return tonumber(current);
                """;
    }
}


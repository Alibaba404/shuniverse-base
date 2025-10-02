package cn.shuniverse.base.config;

import org.apache.tika.Tika;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 蛮小满Sama at 2025-09-26 16:35
 *
 * @author 蛮小满Sama
 * @description Apache Tika 配置
 * 自动提供一个 Tika Bean
 */
@Configuration
public class TikaConfig {

    @Bean
    @ConditionalOnMissingBean(Tika.class)
    public Tika tika() {
        return new Tika();
    }
}

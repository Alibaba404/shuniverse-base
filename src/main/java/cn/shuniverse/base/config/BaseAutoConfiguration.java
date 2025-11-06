package cn.shuniverse.base.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;


/**
 * Created by 蛮小满Sama at 2025-11-06 23:01
 * 公共包的核心配置类，排除数据源自动配置
 *
 * @author 蛮小满Sama
 * @description
 */
@Configuration
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class BaseAutoConfiguration {
    // 公共包的其他配置（如Bean注册、AOP配置等）
}

package cn.shuniverse.base.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Created by 蛮小满Sama at 2025-09-20 14:00
 *
 * @author 蛮小满Sama
 * @description
 */
@Configuration
public class MailConfig {
    /**
     * 创建一个空的 JavaMailSender，用于在邮件功能未配置时抛出异常
     */
    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl() {
            @Override
            public void send(SimpleMailMessage simpleMessage) {
                throw new UnsupportedOperationException("邮件功能未配置，请检查 spring.mail 配置");
            }
        };
    }
}

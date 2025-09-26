package cn.shuniverse.base.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Created by 蛮小满Sama at 2025-09-20 14:00
 *
 * @author 蛮小满Sama
 * @description 邮件配置
 * 没有在 application.yml 里配置 spring.mail.host 时，才会走兜底
 */
@Configuration
@ConditionalOnMissingBean(JavaMailSender.class)
@ConditionalOnProperty(
        prefix = "spring.mail",
        name = "host",
        havingValue = "false",
        matchIfMissing = true
)
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl() {
            @Override
            public void send(SimpleMailMessage simpleMessage) {
                throw new UnsupportedOperationException("邮件功能未配置，请检查 spring.mail 配置");
            }
        };
    }
}

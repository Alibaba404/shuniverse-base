package cn.shuniverse.base.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 蛮小满Sama at 2025-10-19 14:04
 *
 * @author 蛮小满Sama
 * @description
 */
@Data
@Configuration
public class CryptoConfig {
    @Value("${crypto.sm2.serverPrivateKey:}")
    private String serverPrivateKey;
}

package cn.shuniverse.base.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.shuniverse.base.constants.RedisKeyConstants;
import cn.shuniverse.base.core.exception.BisException;
import cn.shuniverse.base.core.resp.RCode;
import cn.shuniverse.base.entity.dto.CaptchaDto;
import cn.shuniverse.base.entity.po.CaptchaPo;
import cn.shuniverse.base.utils.RedisUtil;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.ChineseGifCaptcha;
import com.wf.captcha.SpecCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by 蛮小满Sama at 2025-04-19 15:00
 *
 * @author 蛮小满Sama
 * @description 验证码服务
 */
@Slf4j
@Service
public class CaptchaService {

    @Value("${captcha.classify:spec}")
    private String captchaClassify;

    @Value("${captcha.len:5}")
    private int len;

    private static final String K_VALUE = "text";
    private static final String K_BASE64 = "base64";

    private final MailService mailService;

    @Autowired
    public CaptchaService(MailService mailService) {
        this.mailService = mailService;
    }

    public CaptchaDto captcha(CaptchaPo model) {
        return captcha(model, 5);
    }

    public CaptchaDto captcha(CaptchaPo model, long timeout) {
        return captcha(model, timeout, TimeUnit.MINUTES);
    }

    /**
     * 生成验证码
     *
     * @param model    验证码参数
     * @param timeout  RedisKey过期时间
     * @param timeUnit 过期时间单位
     * @return 验证码数据
     */
    public CaptchaDto captcha(CaptchaPo model, long timeout, TimeUnit timeUnit) {
        Map<String, String> data = this.handleCaptcha(this.captchaClassify, model);
        if (data.isEmpty()) {
            throw BisException.me(RCode.CAPTCHA_CLASSIFY_ERROR);
        }
        String key = IdUtil.fastSimpleUUID();
        // 存入redis并设置过期时间为5分钟
        RedisUtil.set(RedisKeyConstants.CAPTCHA_CODE_PREFIX + key, data.get(K_VALUE), timeout, timeUnit);
        // 将key和base64返回给前端
        return new CaptchaDto(key, data.get(K_BASE64));
    }

    /**
     * 处理验证码
     *
     * @param classify 验证码类型：spec、chinese、chinese_gif、arithmetic
     * @param model    验证码参数
     * @return 验证码数据
     */
    private Map<String, String> handleCaptcha(String classify, CaptchaPo model) {
        Integer width = model.getWidth();
        Integer height = model.getHeight();
        switch (classify) {
            case "spec" -> {
                SpecCaptcha captcha = new SpecCaptcha(width, height, this.len);
                return Map.of(K_VALUE, captcha.text(), K_BASE64, captcha.toBase64());
            }
            case "chinese" -> {
                ChineseCaptcha captcha = new ChineseCaptcha(width, height, this.len);
                return Map.of(K_VALUE, captcha.text(), K_BASE64, captcha.toBase64());
            }
            case "chinese_gif" -> {
                ChineseGifCaptcha captcha = new ChineseGifCaptcha(width, height, this.len);
                return Map.of(K_VALUE, captcha.text(), K_BASE64, captcha.toBase64());
            }
            case "arithmetic" -> {
                ArithmeticCaptcha captcha = new ArithmeticCaptcha(width, height, this.len);
                // 获取运算的公式：3+2=?
                captcha.getArithmeticString();
                return Map.of(K_VALUE, captcha.text(), K_BASE64, captcha.toBase64());
            }
            default -> log.error("未定义的验证码类型: {}", classify);
        }
        return new HashMap<>();
    }

    /**
     * 发送邮箱验证码
     * 参数校验、生成验证码、存入redis并设置过期时间为5分钟、发送邮件
     *
     * @param model 邮箱验证码参数
     */
    public void sendEmailCaptchaCode(CaptchaPo model) {
        Object o = RedisUtil.get(RedisKeyConstants.CAPTCHA_CODE_PREFIX + model.getEmail());
        if (o != null) {
            throw BisException.me(RCode.USER_CAPTCHA_SEND_ERROR);
        }
        // 生成验证码
        String captchaCode = RandomUtil.randomStringUpper(6);
        // 存入redis并设置过期时间为5分钟
        RedisUtil.set(RedisKeyConstants.CAPTCHA_CODE_PREFIX + model.getEmail(), captchaCode, 5, TimeUnit.MINUTES);
        try {
            mailService.sendHtmlMessage(
                    new String[]{model.getEmail()},
                    "Send from Shuniverse.cn!",
                    String.format("<h1 style='margin:0 auto;'>您的验证码是: %s</h1>", captchaCode));
        } catch (MessagingException e) {
            throw BisException.me(RCode.FAILED);
        }
    }
}

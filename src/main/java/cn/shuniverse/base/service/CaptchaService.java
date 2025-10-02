package cn.shuniverse.base.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.shuniverse.base.constants.RedisKeyConstants;
import cn.shuniverse.base.core.exception.BisException;
import cn.shuniverse.base.core.resp.RCode;
import cn.shuniverse.base.entity.dto.CaptchaDto;
import cn.shuniverse.base.entity.po.CaptchaPo;
import cn.shuniverse.base.utils.RedisUtil;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.concurrent.TimeUnit;

/**
 * Created by 蛮小满Sama at 2025-04-19 15:00
 *
 * @author 蛮小满Sama
 * @description 验证码服务
 */
@Service
public class CaptchaService {
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

    public CaptchaDto captcha(CaptchaPo model, long timeout, TimeUnit timeUnit) {
        return captcha(model, timeout, timeUnit, 5);
    }

    public CaptchaDto captcha(CaptchaPo model, long timeout, TimeUnit timeUnit, int len) {
        SpecCaptcha specCaptcha = new SpecCaptcha(model.getWidth(), model.getHeight(), len);
        String verCode = specCaptcha.text().toLowerCase();
        String key = IdUtil.fastSimpleUUID();
        // 存入redis并设置过期时间为5分钟
        RedisUtil.set(RedisKeyConstants.CAPTCHA_CODE_PREFIX + key, verCode, timeout, timeUnit);
        // 将key和base64返回给前端
        return new CaptchaDto(key, specCaptcha.toBase64());
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

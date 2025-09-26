package cn.shuniverse.base.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by 蛮小满Sama at 2025-06-23 15:15
 *
 * @author 蛮小满Sama
 * @description 邮件服务
 */
@Service
public class MailService {

    private final JavaMailSender emailSender;

    @Autowired
    public MailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Value("${spring.mail.username:Shuniverse}")
    private String defaultSendFromEmail;

    /**
     * 发送简单邮件(支持多方发送)
     *
     * @param to      收件人邮箱地址
     * @param subject 邮件主题
     * @param text    发送内容
     */
    public void sendSimpleMessage(String[] to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(defaultSendFromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    /**
     * 发送带html格式的邮件(支持多方发送)
     *
     * @param to          收件人邮箱地址
     * @param subject     邮件主题
     * @param htmlContent 邮件正文(带html格式)
     * @throws MessagingException 邮件发送异常
     */
    public void sendHtmlMessage(String[] to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        // 发送方(必须和配置文件中一致)
        helper.setFrom(defaultSendFromEmail);
        // 接收方
        helper.setTo(to);
        // 邮件主题
        helper.setSubject(subject);
        // 设置为true，表示内容是HTML格式
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    /**
     * 发送带附件的邮件(支持多方发送)
     *
     * @param to                    收件人邮箱地址
     * @param subject               邮件主题
     * @param htmlContent           邮件正文(带html格式)
     * @param attachmentName        附件名称
     * @param attachmentData        附件数据
     * @param attachmentContentType 附件内容类型
     *                              application/pdf
     *                              application/msword
     *                              application/vnd.openxmlformats-officedocument.wordprocessingml.document
     *                              image/jpeg
     *                              image/png
     *                              text/plain
     *                              text/csv
     *                              audio/mpeg
     *                              video/
     * @throws MessagingException 邮件发送异常
     */
    public void sendEmailWithAttachment(
            String[] to,
            String subject,
            String htmlContent,
            String attachmentName,
            byte[] attachmentData,
            String attachmentContentType
    ) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        // 发送方(必须和配置文件中一致)
        helper.setFrom(defaultSendFromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        // 添加附件
        Resource attachment = new ByteArrayResource(attachmentData);
        helper.addAttachment(attachmentName, attachment, attachmentContentType);
        emailSender.send(message);
    }

    /**
     * 发送带附件的邮件(支持多方发送)
     *
     * @param to          收件人邮箱地址
     * @param subject     邮件主题
     * @param htmlContent 邮件正文(带html格式)
     * @param files       附件数据
     * @throws MessagingException 邮件发送异常
     */
    public void sendEmailWithAttachment(
            String[] to,
            String subject,
            String htmlContent,
            List<File> files
    ) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        // 发送方(必须和配置文件中一致)
        helper.setFrom(defaultSendFromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        if (!CollectionUtils.isEmpty(files)) {
            // 添加附件
            for (File file : files) {
                helper.addAttachment(file.getName(), file);
            }
        }
        emailSender.send(message);
    }
}

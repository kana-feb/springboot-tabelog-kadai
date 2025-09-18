package com.example.nagoyameshi.event;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.mail.MailSenderAddressResolver;
import com.example.nagoyameshi.service.PasswordResetTokenService;

@Component
public class PasswordResetEventListener {
    private final PasswordResetTokenService passwordResetTokenService;
    private final JavaMailSender javaMailSender;
    private final MailSenderAddressResolver mailSenderAddressResolver;
    private final String configuredSenderAddress;

    public PasswordResetEventListener(PasswordResetTokenService passwordResetTokenService,
            JavaMailSender javaMailSender,
            MailSenderAddressResolver mailSenderAddressResolver,
            @Value("${nagoyameshi.mail.from-address:${spring.mail.username:}}") String senderAddress) {
        this.passwordResetTokenService = passwordResetTokenService;
        this.javaMailSender = javaMailSender;
        this.mailSenderAddressResolver = mailSenderAddressResolver;
        this.configuredSenderAddress = senderAddress;
    }

    @EventListener
    public void onPasswordResetEvent(PasswordResetEvent passwordResetEvent) {
        User user = passwordResetEvent.getUser();
        String token = UUID.randomUUID().toString();
        passwordResetTokenService.create(user, token);

        String recipientAddress = passwordResetEvent.getRequestEmail();
        String subject = "パスワード再設定";
        String confirmationUrl = passwordResetEvent.getRequestUrl() + "/edit?token=" + token;
        String message = "以下のリンクをクリックしてパスワードを再設定してください。";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailSenderAddressResolver.resolve(javaMailSender, configuredSenderAddress)
                .ifPresent(mailMessage::setFrom);
        mailMessage.setTo(recipientAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(message + "\n" + confirmationUrl);
        javaMailSender.send(mailMessage);
    }
}
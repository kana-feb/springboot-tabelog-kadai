package com.example.nagoyameshi.event;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.mail.MailSenderAddressResolver;
import com.example.nagoyameshi.service.VerificationTokenService;

@Component
public class SignupEventListener {
    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender javaMailSender;
    private final MailSenderAddressResolver mailSenderAddressResolver;
    private final String configuredSenderAddress;

    public SignupEventListener(VerificationTokenService verificationTokenService,
            JavaMailSender mailSender,
            MailSenderAddressResolver mailSenderAddressResolver,
            @Value("${nagoyameshi.mail.from-address:${spring.mail.username:}}") String senderAddress) {
        this.verificationTokenService = verificationTokenService;
        this.javaMailSender = mailSender;
        this.mailSenderAddressResolver = mailSenderAddressResolver;
        this.configuredSenderAddress = senderAddress;
    }

    //@EventListener:イベントリスナークラスは、特定のイベント発生を受けて処理を実行するクラス
    @EventListener
    //通知を受け付けるEventクラスをメソッドの引数に設定する
    private void onSignupEvent(SignupEvent signupEvent) {
        User user = signupEvent.getUser();
        String token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "メール認証";
        String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;
        String message = "以下のリンクをクリックして会員登録を完了してください。";

        //SimpleMailMessageクラスを使ってメール内容を作成する
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailSenderAddressResolver.resolve(javaMailSender, configuredSenderAddress)
                .ifPresent(mailMessage::setFrom);//setFrom()：送信元のメールアドレスをセットする
        mailMessage.setTo(recipientAddress);//setTo()：送信先のメールアドレスをセットする
        mailMessage.setSubject(subject);//件名をセット
        mailMessage.setText(message + "\n" + confirmationUrl);//本文をセット
        javaMailSender.send(mailMessage);//javaMailSenderインターフェースを使ってメールを送信する

    }
}
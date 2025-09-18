package com.example.nagoyameshi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@RestController
public class StripeWebhookController {

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Stripe の署名検証
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            // イベントの種類ごとに分岐
            switch (event.getType()) {
                case "checkout.session.completed":
                    System.out.println("✅ 決済完了イベントを受信");
                    // ここでDB更新やロール変更などを実行
                    break;
                case "invoice.payment_failed":
                    System.out.println("⚠️ 決済失敗イベントを受信");
                    break;
                default:
                    System.out.println("ℹ️ 他イベント: " + event.getType());
            }

            return ResponseEntity.ok("success");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
    }
}

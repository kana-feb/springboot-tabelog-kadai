package com.example.nagoyameshi.mail;

import java.util.Optional;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class MailSenderAddressResolver {

    public Optional<String> resolve(JavaMailSender mailSender, String configuredAddress) {
        String candidate = trimToNull(configuredAddress);
        if (candidate != null) {
            return Optional.of(candidate);
        }

        if (mailSender instanceof JavaMailSenderImpl mailSenderImpl) {
            candidate = trimToNull(mailSenderImpl.getUsername());
            if (candidate != null) {
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
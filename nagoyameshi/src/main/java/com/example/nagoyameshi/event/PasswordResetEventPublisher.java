package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;

@Component
public class PasswordResetEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;
	
	public PasswordResetEventPublisher ( ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	public void publishPasswordResetEvent(User user, String requestUrl, String requestEmail) {
		applicationEventPublisher.publishEvent(new PasswordResetEvent(this, user, requestUrl, requestEmail));
	}

}


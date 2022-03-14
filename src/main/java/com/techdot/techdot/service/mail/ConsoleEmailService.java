package com.techdot.techdot.service.mail;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Profile("local")
@Component
@Slf4j
public class ConsoleEmailService implements EmailService{

	@Override
	public void sendEmail(EmailMessageDto emailMessageDto) {
		log.info("{} time, send confirm email : {}", emailMessageDto.getSendTime(), emailMessageDto.getMessage());
	}
}

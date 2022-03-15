package com.techdot.techdot.infra.mail;

public interface EmailService {
	void sendEmail(EmailMessageDto emailMessageDto);
}

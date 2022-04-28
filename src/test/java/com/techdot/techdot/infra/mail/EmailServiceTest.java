package com.techdot.techdot.infra.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techdot.techdot.infra.mail.EmailMessageDto;
import com.techdot.techdot.infra.mail.EmailService;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

	private final EmailService emailService = new EmailService() {
		@Override
		public void sendEmail(EmailMessageDto emailMessageDto) {
			System.out.printf( emailMessageDto.getSendTime() + " time, send confirm email : " + emailMessageDto.getMessage());
		}
	};


	@DisplayName("이메일 전송하기")
	@Test
	void sendEmail(){
		EmailMessageDto emailMessageDto = EmailMessageDto.builder()
			.to("to")
			.subject("subject")
			.message("message")
			.sendTime(1)
			.build();
		emailService.sendEmail(emailMessageDto);
	}
}

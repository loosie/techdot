package com.techdot.techdot.service.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Profile("dev")
@Component
@Slf4j
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService{

	private final JavaMailSender javaMailSender;
	@Override
	public void sendEmail(EmailMessageDto emailMessageDto) {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(emailMessageDto.getTo());
			mimeMessageHelper.setSubject(emailMessageDto.getSubject());
			mimeMessageHelper.setText(emailMessageDto.getMessage(), true);
			javaMailSender.send(mimeMessage);
			log.info("send confirm email : {} - time : {} ", emailMessageDto.getTo(), emailMessageDto.getSendTime());
		} catch (MessagingException ex) {
			log.error("failed to send email : " + ex.getMessage());
		}

	}
}

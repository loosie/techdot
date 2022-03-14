package com.techdot.techdot.service.mail;

import lombok.Builder;
import lombok.Data;

@Data
public class EmailMessageDto {
	private String to;
	private String subject;
	private String message;
	private Integer sendTime;

	@Builder
	public EmailMessageDto(String to, String subject, String message, Integer sendTime) {
		this.to = to;
		this.subject = subject;
		this.message = message;
		this.sendTime = sendTime;
	}
}

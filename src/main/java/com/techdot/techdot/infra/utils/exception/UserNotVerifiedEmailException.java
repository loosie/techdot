package com.techdot.techdot.infra.utils.exception;

import lombok.Getter;

@Getter
public class UserNotVerifiedEmailException extends RuntimeException{
	private final String viewName;
	private final String email;
	public UserNotVerifiedEmailException(String message, String viewName, String email){
		super(message);
		this.viewName = viewName;
		this.email = email;
	}
}

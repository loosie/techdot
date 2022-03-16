package com.techdot.techdot.infra.exception;

import lombok.Getter;

@Getter
public class UserNotExistedException extends RuntimeException{
	private final String viewName;
	public UserNotExistedException(String message, String viewName){
		super(message);
		this.viewName = viewName;
	}
}

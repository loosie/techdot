package com.techdot.techdot.modules.main;

import lombok.Getter;

@Getter
public class UserNotExistedException extends RuntimeException{
	private final String viewName;
	public UserNotExistedException(final String message, final String viewName){
		super(message);
		this.viewName = viewName;
	}
}

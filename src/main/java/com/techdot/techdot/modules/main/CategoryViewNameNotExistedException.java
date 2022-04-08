package com.techdot.techdot.modules.main;

import lombok.Getter;

@Getter
public class CategoryViewNameNotExistedException extends RuntimeException{
	public CategoryViewNameNotExistedException(final String message){
		super(message);
	}
}

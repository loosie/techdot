package com.techdot.techdot.modules.main.exception;

import lombok.Getter;

@Getter
public class CategoryCanNotDeleteException extends RuntimeException{
	public CategoryCanNotDeleteException(final String message){
		super(message);
	}
}

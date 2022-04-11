package com.techdot.techdot.modules.main;

import lombok.Getter;

@Getter
public class CategoryCanNotDeleteException extends RuntimeException{
	public CategoryCanNotDeleteException(final String message){
		super(message);
	}
}

package com.techdot.techdot.modules.main.exception;

import lombok.Getter;

@Getter
public class CategoryNotExistedException extends RuntimeException{
	public CategoryNotExistedException(final String name) {
		super(name +" 해당 카테고리는 존재하지 않습니다.");
	}
}

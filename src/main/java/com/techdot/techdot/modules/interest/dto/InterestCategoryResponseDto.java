package com.techdot.techdot.modules.interest.dto;

import lombok.Data;

@Data
public class InterestCategoryResponseDto {
	private String categoryViewName;

	public InterestCategoryResponseDto(final String categoryViewName) {
		this.categoryViewName = categoryViewName;
	}
}

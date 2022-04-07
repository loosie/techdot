package com.techdot.techdot.modules.interest.dto;

import com.techdot.techdot.modules.category.CategoryName;

import lombok.Data;

@Data
public class InterestCategoryResponseDto {
	private String categoryName;

	public InterestCategoryResponseDto(String categoryName) {
		this.categoryName = categoryName;
	}
}

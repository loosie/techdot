package com.techdot.techdot.module.interest.dto;

import com.techdot.techdot.module.category.CategoryName;

import lombok.Data;

@Data
public class InterestCategoryResponseDto {
	private CategoryName categoryName;

	public InterestCategoryResponseDto(CategoryName categoryName) {
		this.categoryName = categoryName;
	}
}

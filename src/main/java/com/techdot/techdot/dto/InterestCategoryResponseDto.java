package com.techdot.techdot.dto;

import com.techdot.techdot.domain.CategoryName;

import lombok.Data;

@Data
public class InterestCategoryResponseDto {
	private CategoryName categoryName;

	public InterestCategoryResponseDto(CategoryName categoryName) {
		this.categoryName = categoryName;
	}
}

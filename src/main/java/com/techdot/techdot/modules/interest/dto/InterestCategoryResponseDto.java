package com.techdot.techdot.modules.interest.dto;

import com.techdot.techdot.modules.post.CategoryName;

import lombok.Data;

@Data
public class InterestCategoryResponseDto {
	private CategoryName categoryName;

	public InterestCategoryResponseDto(CategoryName categoryName) {
		this.categoryName = categoryName;
	}
}
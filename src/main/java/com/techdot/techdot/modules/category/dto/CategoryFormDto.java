package com.techdot.techdot.modules.category.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryFormDto {
	@NotBlank
	@Length(max = 20)
	private String name;

	@NotBlank
	@Length(max = 20)
	private String title;

	@NotBlank
	@Length(max = 20)
	private String viewName;

	public CategoryFormDto(String name, String title, String viewName) {
		this.name = name;
		this.title = title;
		this.viewName = viewName;
	}
}

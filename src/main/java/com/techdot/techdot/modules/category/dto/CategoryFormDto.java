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

	public CategoryFormDto(String name) {
		this.name = name;
	}
}

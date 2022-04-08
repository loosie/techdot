package com.techdot.techdot.modules.category.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
	@Pattern(regexp = "^[a-z,-]{1,20}$", message = "공백없이 영어 소문자와 - 만 20자 내외로 입력이 가능합니다.")
	private String viewName;

	public CategoryFormDto(String name, String title, String viewName) {
		this.name = name;
		this.title = title;
		this.viewName = viewName;
	}
}

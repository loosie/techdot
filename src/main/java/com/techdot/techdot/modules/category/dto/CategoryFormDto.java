package com.techdot.techdot.modules.category.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.techdot.techdot.modules.category.Category;

import lombok.Builder;
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
	@Pattern(regexp = "^[a-z0-9,-]{1,20}$", message = "공백없이 영어 소문자, 숫자와 - 만 20자 내외로 입력이 가능합니다.")
	private String viewName;

	private int curDisplayOrder;
	private int displayOrder;

	private String curName;
	private String curTitle;
	private String curViewName;

	public CategoryFormDto(final Category category) {
		this.curName = category.getName();
		this.curTitle = category.getTitle();
		this.curViewName = category.getViewName();
		this.curDisplayOrder = category.getDisplayOrder();
		this.name = category.getName();
		this.title = category.getTitle();
		this.viewName = category.getViewName();
		this.displayOrder = category.getDisplayOrder();
	}
}

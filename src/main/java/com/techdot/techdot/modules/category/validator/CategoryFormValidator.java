package com.techdot.techdot.modules.category.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.category.dto.CategoryFormDto;
import com.techdot.techdot.modules.post.dto.PostFormDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryFormValidator implements Validator {

	private final CategoryRepository categoryRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return CategoryFormDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CategoryFormDto categoryForm = (CategoryFormDto)target;

		if(categoryRepository.existsByName(categoryForm.getName())){
			errors.rejectValue("name", "invalid.name", "이미 등록된 name 입니다.");
		}

		if(categoryRepository.existsByTitle(categoryForm.getTitle())){
			errors.rejectValue("title", "invalid.title", "이미 등록된 title 입니다.");
		}

		if(categoryRepository.existsByViewName(categoryForm.getViewName())){
			errors.rejectValue("viewName", "invalid.viewName", "이미 등록된 viewName 입니다.");
		}

	}
}

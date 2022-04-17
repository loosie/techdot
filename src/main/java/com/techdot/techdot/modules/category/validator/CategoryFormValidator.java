package com.techdot.techdot.modules.category.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.category.dto.CategoryFormDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryFormValidator implements Validator {

	private final CategoryRepository categoryRepository;

	@Override
	public boolean supports(final Class<?> clazz) {
		return CategoryFormDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		CategoryFormDto categoryForm = (CategoryFormDto)target;

		// 생성: 새로 생성하는 데이터 중복 검사
		if(categoryForm.getCurTitle() == null){
			validateTitle(errors, categoryForm);
			validateName(errors, categoryForm);
			validateViewName(errors, categoryForm);
		}else{
			// 수정: 수정된 데이터 중복 검사
			if(!categoryForm.getCurTitle().equals(categoryForm.getTitle())){
				validateTitle(errors, categoryForm);
			}

			if(!categoryForm.getCurName().equals(categoryForm.getName())){
				validateName(errors, categoryForm);
			}

			if(!categoryForm.getCurViewName().equals(categoryForm.getViewName())){
				validateViewName(errors, categoryForm);
			}
		}
	}

	private void validateViewName(final Errors errors, final CategoryFormDto categoryForm) {
		if(categoryRepository.existsByViewName(categoryForm.getViewName())){
			errors.rejectValue("viewName", "invalid.viewName", "이미 등록된 viewName 입니다.");
		}
	}

	private void validateName(final Errors errors, final CategoryFormDto categoryForm) {
		if(categoryRepository.existsByName(categoryForm.getName())){
			errors.rejectValue("name", "invalid.name", "이미 등록된 name 입니다.");
		}
	}

	private void validateTitle(final Errors errors, final CategoryFormDto categoryForm) {
		if(categoryRepository.existsByTitle(categoryForm.getTitle())){
			errors.rejectValue("title", "invalid.title", "이미 등록된 title 입니다.");
		}
	}
}

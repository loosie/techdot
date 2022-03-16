package com.techdot.techdot.modules.member.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.techdot.techdot.modules.member.dto.PasswordFormDto;

public class PasswordFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(PasswordFormDto.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PasswordFormDto passwordForm = (PasswordFormDto)target;
		if(!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())){
			errors.rejectValue("newPassword", "unmatched.password", "입력한 비밀번호가 일치하지 않습니다.");
			return;
		}

	}
}

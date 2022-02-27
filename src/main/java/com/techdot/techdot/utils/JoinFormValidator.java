package com.techdot.techdot.utils;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.techdot.techdot.domain.MemberRepo;
import com.techdot.techdot.dto.MemberJoinFormDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JoinFormValidator implements Validator {

	private final MemberRepo memberRepo;

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(MemberJoinFormDto.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		MemberJoinFormDto joinForm = (MemberJoinFormDto)target;
		if(memberRepo.existsByEmail(joinForm.getEmail())){
			errors.rejectValue("email", "invalid.email", new Object[]{joinForm.getEmail()}, "이미 사용중인 이메일입니다.");
			return;
		}

		if(memberRepo.existsByNickname(joinForm.getNickname())){
			errors.rejectValue("nickname", "invalid.nickname", new Object[]{joinForm.getNickname()}, "이미 사용중인 닉네임입니다.");
			return;
		}

		if(!joinForm.getPassword().equals(joinForm.getPasswordConfirm())){
			errors.rejectValue("passwordConfirm", "invalid.passwordConfirm", new Object[]{joinForm.getPasswordConfirm()}, "비밀번호가 일치하지 않습니다.");
			return;
		}

		if(!joinForm.getTermsCheck()){
			errors.rejectValue("termsCheck", "invalid.terms", new Object[]{joinForm.getTermsCheck()}, "약관 동의에 체크해주세요.");
			return;
		}


	}
}

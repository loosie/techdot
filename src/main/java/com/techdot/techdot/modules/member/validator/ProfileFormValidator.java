package com.techdot.techdot.modules.member.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.member.dto.ProfileFormDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileFormValidator implements Validator {

	private final MemberRepository memberRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return ProfileFormDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ProfileFormDto profileForm = (ProfileFormDto)target;
		if (profileForm.getCurNickname().equals(profileForm.getNewNickname())) {
			return;
		}

		boolean existed = memberRepository.existsByNickname(profileForm.getNewNickname());
		if (existed) {
			errors.rejectValue("newNickname", "duplicate.value", "이미 사용중인 닉네임입니다.");
		}

	}
}

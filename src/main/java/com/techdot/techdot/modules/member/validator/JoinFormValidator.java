package com.techdot.techdot.modules.member.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.member.dto.JoinFormDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JoinFormValidator implements Validator {

	private final MemberRepository memberRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(JoinFormDto.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		JoinFormDto joinForm = (JoinFormDto)target;
		if (memberRepository.existsByNickname(joinForm.getNickname())) {
			errors.rejectValue("nickname", "invalid.nickname", new Object[] {joinForm.getNickname()},
				"이미 사용중인 닉네임입니다.");
		}

		if (memberRepository.existsByEmail(joinForm.getEmail())) {
			errors.rejectValue("email", "invalid.email", new Object[] {joinForm.getEmail()}, "이미 사용중인 이메일입니다.");
		}

		if (!joinForm.getPassword().equals(joinForm.getPasswordConfirm())) {
			errors.rejectValue("passwordConfirm", "unmatched.password", new Object[] {joinForm.getPasswordConfirm()},
				"비밀번호가 일치하지 않습니다.");
		}

	}
}

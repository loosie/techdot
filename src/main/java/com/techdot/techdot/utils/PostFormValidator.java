package com.techdot.techdot.utils;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.techdot.techdot.dto.PostFormDto;
import com.techdot.techdot.dto.ProfileFormDto;
import com.techdot.techdot.repository.MemberRepository;
import com.techdot.techdot.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostFormValidator implements Validator {

	private final PostRepository postRepo;

	@Override
	public boolean supports(Class<?> clazz) {
		return PostFormDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PostFormDto postForm = (PostFormDto)target;

		if(postRepo.existsByLink(postForm.getLink())){
			errors.rejectValue("link", "duplicate.link", "이미 등록된 url입니다.");
		}
	}
}

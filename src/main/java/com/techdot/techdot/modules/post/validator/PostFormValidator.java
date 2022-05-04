package com.techdot.techdot.modules.post.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.techdot.techdot.modules.post.dto.PostFormDto;
import com.techdot.techdot.modules.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostFormValidator implements Validator {

	private final PostRepository postRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return PostFormDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		PostFormDto postForm = (PostFormDto)target;

		// 변경되지 않은 링크는 중복 처리 예외
		if (!postForm.getCurLink().isEmpty() && postForm.getCurLink().equals(postForm.getLink())) {
			return;
		}

		if (postRepository.existsByLink(postForm.getLink())) {
			errors.rejectValue("link", "invalid.link", "이미 등록된 url입니다.");
		}
	}
}

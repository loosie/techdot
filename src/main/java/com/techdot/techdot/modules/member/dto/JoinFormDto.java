package com.techdot.techdot.modules.member.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class JoinFormDto {

	@NotBlank
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣A-z0-9]{3,20}$", message = "공백없이 문자와 숫자로만 3자 이상 20자 이내로 입력이 가능합니다.")
	@Length(min = 3, max = 20)
	private String nickname;

	@Email
	@NotBlank
	private String email;

	@NotBlank
	@Length(min = 8, max = 50)
	private String password;

	@NotBlank
	@Length(min = 8, max = 50)
	private String passwordConfirm;
}

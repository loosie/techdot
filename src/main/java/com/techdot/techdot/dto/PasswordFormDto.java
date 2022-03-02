package com.techdot.techdot.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.techdot.techdot.domain.Member;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PasswordFormDto {

	@NotBlank
	@Length(min=8, max =50)
	private String newPassword;

	@NotBlank
	@Length(min=8, max =50)
	private String newPasswordConfirm;


}

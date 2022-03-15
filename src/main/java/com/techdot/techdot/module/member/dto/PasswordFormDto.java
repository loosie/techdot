package com.techdot.techdot.module.member.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class PasswordFormDto {

	@NotBlank
	@Length(min=8, max =50)
	private String newPassword;

	@NotBlank
	@Length(min=8, max =50)
	private String newPasswordConfirm;


}

package com.techdot.techdot.controller.dto;

import lombok.Getter;

@Getter
public class MemberJoinRequestDto {
	private String nickname;
	private String email;
	private String password;
	private String passwordConfirm;
	private Boolean termsCheck;
}

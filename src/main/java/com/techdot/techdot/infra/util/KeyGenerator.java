package com.techdot.techdot.infra.util;


public class KeyGenerator {
	private static final String EMAIL_CHECK_TOKEN = "emailCheckToken";
	private static final String LOGIN_TOKEN = "loginToken";

	public static String generateEmailCheckTokenKey(Long memberId) {
		return EMAIL_CHECK_TOKEN + ":" + memberId;
	}

	public static String generateLoginTokenKey(Long memberId) {
		return LOGIN_TOKEN + ":" + memberId;
	}
}

package com.techdot.techdot.infra.util;

import java.util.UUID;

public class TokenGenerator {

	public static String generateToken() {
		return UUID.randomUUID().toString();
	}
}

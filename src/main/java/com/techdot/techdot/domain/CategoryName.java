package com.techdot.techdot.domain;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum CategoryName {
	CS("CS", "cs"),
	BACKEND("Backend", "backend"),
	FRONTEND("Frontend", "frontend"),
	SECURITY("Security", "security"),
	DEV_OPS("DevOps", "dev-ops"),
	MOTIVATION("Motivation", "motivation");

	private final String displayValue;
	private final String viewName;

	private CategoryName(String displayValue, String viewName) {
		this.displayValue = displayValue;
		this.viewName = viewName;
	}

	public static String getMainViewName(String viewName) {
		return "/main/"+ viewName;
	}
}

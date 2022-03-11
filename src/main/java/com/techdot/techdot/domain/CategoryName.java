package com.techdot.techdot.domain;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum CategoryName {
	CS("cs", "/main/cs"),
	Backend("backend", "/main/backend"),
	Frontend("frontend", "/main/frontend"),
	Security("security", "/main/security"),
	DevOps("dev-ops", "/main/dev-ops"),
	Motivation("motivation", "/main/motivation");

	private final String displayValue;
	private final String viewName;

	private CategoryName(String displayValue, String viewName) {
		this.displayValue = displayValue;
		this.viewName = viewName;
	}

	public static String getViewName(String categoryName) {
		return Arrays.stream(CategoryName.values())
			.filter(c -> categoryName.equals(c.getDisplayValue()))
			.findFirst()
			.orElseThrow(NullPointerException::new)
			.getViewName();
	}
}

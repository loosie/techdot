package com.techdot.techdot.domain;

import lombok.Getter;

@Getter
public enum CategoryName {
	CS("CS", "/main/cs"),
	Backend("Backend", "/main/backend"),
	Frontend("Frontend", "/main/frontend"),
	Security("Security", "/main/security"),
	DevOps("DevOps", "/main/devops"),
	Motivation("Motivation", "/main/motivation");

	private final String displayValue;
	private final String viewName;

	private CategoryName(String displayValue, String viewName) {
		this.displayValue = displayValue;
		this.viewName = viewName;
	}
}

package com.techdot.techdot.domain;

import lombok.Getter;

@Getter
public enum CategoryName {
	CS("CS"),
	Algorithm("Algorithm"),
	Backend("Backend"),
	Frontend("Frontend"),
	Security("Security"),
	DevOps("DevOps"),
	Motivation("Motivation");

	public final String displayValue;

	private CategoryName(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
}

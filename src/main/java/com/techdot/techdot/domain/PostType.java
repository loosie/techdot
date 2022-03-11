package com.techdot.techdot.domain;

import lombok.Getter;

@Getter
public enum PostType {
	BLOG("Blog"), VIDEO("Video");

	private final String displayValue;

	private PostType(String displayValue) {
		this.displayValue = displayValue;
	}
}

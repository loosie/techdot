package com.techdot.techdot.modules.post.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MyUploadPostResponseDto {
	private Long id;
	private String title;
	private String writer;
	private LocalDateTime uploadDateTime;
	private String categoryName;

	public MyUploadPostResponseDto(final Long id, final String title, final String writer, final LocalDateTime uploadDateTime,
		final String categoryName) {
		this.id = id;
		this.title = title;
		this.writer = writer;
		this.uploadDateTime = uploadDateTime;
		this.categoryName = categoryName;
	}
}

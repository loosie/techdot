package com.techdot.techdot.dto;

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.PostType;

import lombok.Data;

@Data
public class PostQueryDto {
	private Long postId;
	private String title;
	private String content;
	private String link;
	private String writer;
	private PostType type;
	private String thumbnailImage;
	private CategoryName categoryName;

	public PostQueryDto(Long postId, String title, String content, String link, String writer, PostType type, String thumbnailImage, CategoryName categoryName) {
		this.postId = postId;
		this.title = title;
		this.content = content;
		this.link = link;
		this.writer = writer;
		this.type = type;
		this.thumbnailImage = thumbnailImage;
		this.categoryName = categoryName;
	}
}

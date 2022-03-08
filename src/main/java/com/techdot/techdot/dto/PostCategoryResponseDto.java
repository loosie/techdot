package com.techdot.techdot.dto;

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.PostType;

import lombok.Builder;
import lombok.Data;

@Data
public class PostCategoryResponseDto {
	private Long postId;
	private String title;
	private String content;
	private String link;
	private String writer;
	private PostType type;
	private String thumbnailImage;
	private CategoryName categoryName;
	private Boolean isLike;

	@Builder
	public PostCategoryResponseDto(Long postId, String title, String content, String link, String writer, PostType type, String thumbnailImage, CategoryName categoryName,
		Boolean isLike) {
		this.postId = postId;
		this.title = title;
		this.content = content;
		this.link = link;
		this.writer = writer;
		this.type = type;
		this.thumbnailImage = thumbnailImage;
		this.categoryName = categoryName;
		this.isLike = isLike;
	}
}

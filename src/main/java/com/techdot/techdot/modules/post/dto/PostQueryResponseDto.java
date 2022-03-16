package com.techdot.techdot.modules.post.dto;

import java.time.LocalDateTime;

import com.techdot.techdot.modules.post.CategoryName;
import com.techdot.techdot.modules.post.PostType;

import lombok.Data;

@Data
public class PostQueryResponseDto {
	private Long postId;
	private String title;
	private String content;
	private String link;
	private String writer;
	private PostType type;
	private String thumbnailImage;
	private LocalDateTime uploadDateTime;
	private String categoryName;
	private Boolean isMemberLike;

	public PostQueryResponseDto(Long postId, String title, String content, String link, String writer, PostType type, String thumbnailImage,
		LocalDateTime uploadDateTime, CategoryName categoryName, Boolean isMemberLike) {
		this.postId = postId;
		this.title = title;
		this.content = content;
		this.link = link;
		this.writer = writer;
		this.type = type;
		this.thumbnailImage = thumbnailImage;
		this.uploadDateTime = uploadDateTime;
		this.categoryName = categoryName.getDisplayValue();
		this.isMemberLike = isMemberLike;
	}

}
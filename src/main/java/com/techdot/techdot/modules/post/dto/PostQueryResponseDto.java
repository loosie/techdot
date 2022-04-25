package com.techdot.techdot.modules.post.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.techdot.techdot.modules.post.PostType;

import lombok.Data;

@Data
public class PostQueryResponseDto implements Serializable {
	private Long postId;
	private String title;
	private String content;
	private String link;
	private String writer;
	private PostType type;
	private String thumbnailImageUrl;
	private LocalDateTime uploadDateTime;
	private String categoryDisplayName;
	private Boolean isMemberLike;

	public PostQueryResponseDto(final Long postId, final String title,final  String content,
		final String link, final String writer, final PostType type, final String thumbnailImageUrl,
		final LocalDateTime uploadDateTime, final String categoryDisplayName, final Boolean isMemberLike) {
		this.postId = postId;
		this.title = title;
		this.content = content;
		this.link = link;
		this.writer = writer;
		this.type = type;
		this.thumbnailImageUrl = thumbnailImageUrl;
		this.uploadDateTime = uploadDateTime;
		this.categoryDisplayName = categoryDisplayName;
		this.isMemberLike = isMemberLike;
	}

}

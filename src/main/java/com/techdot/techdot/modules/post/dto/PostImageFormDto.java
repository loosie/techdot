package com.techdot.techdot.modules.post.dto;

import com.techdot.techdot.modules.post.Post;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostImageFormDto {

	private String thumbnailImageUrl;

	public PostImageFormDto(final Post post) {
		this.thumbnailImageUrl = post.getThumbnailImageUrl();
	}
}

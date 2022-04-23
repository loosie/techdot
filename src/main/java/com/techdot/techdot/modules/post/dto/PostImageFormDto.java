package com.techdot.techdot.modules.post.dto;

import com.techdot.techdot.modules.post.Post;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostImageFormDto {

	private String thumbnailImageUrl;

	public PostImageFormDto(final Post post) {
		if(post.getThumbnailImageUrl() != null && !post.getThumbnailImageUrl().isEmpty()){
			this.thumbnailImageUrl = "https://s3.ap-northeast-2.amazonaws.com/cdn.techdot.info/static/" + post.getThumbnailImageUrl();
		}
	}
}

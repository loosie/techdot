package com.techdot.techdot.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.techdot.techdot.domain.Post;
import com.techdot.techdot.domain.PostType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostFormDto {

	@NotBlank
	@Length(max = 50)
	private String title;

	@NotBlank
	@Length(max = 100)
	private String content;

	@NotBlank
	private String link;

	private PostType type = PostType.BLOG;

	private String thumbnailImage;

	public PostFormDto(Post post) {
		this.title = post.getTitle();
		this.content = post.getContent();
		this.link = post.getLink();
		this.type = post.getType();
		this.thumbnailImage = post.getThumbnailImage();
	}
}

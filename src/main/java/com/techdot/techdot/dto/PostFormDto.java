package com.techdot.techdot.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.domain.PostType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostFormDto {

	@NotBlank
	@Length(max = 100)
	private String title;

	@NotBlank
	@Length(max = 100)
	private String content;

	private String beforeLink;

	@NotBlank
	@Pattern(regexp = "https?://(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)", message = "올바른 입력 형태가 아닙니다.")
	private String link;

	@NotBlank
	private String writer;

	private PostType type;

	private CategoryName categoryName;

	private String thumbnailImage;

	public PostFormDto(Post post) {
		this.title = post.getTitle();
		this.content = post.getContent();
		this.beforeLink = post.getLink();
		this.link = post.getLink();
		this.writer = post.getWriter();
		this.type = post.getType();
		this.categoryName = post.getCategory().getName();
		this.thumbnailImage = post.getThumbnailImage();
	}
}

package com.techdot.techdot.module.post.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import com.techdot.techdot.module.category.CategoryName;
import com.techdot.techdot.module.post.Post;
import com.techdot.techdot.module.post.PostType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostFormDto {

	@NotBlank
	@Length(max = 100)
	private String title;

	@NotBlank
	@Length(max = 200)
	private String content;

	private String beforeLink;

	@NotBlank
	@Pattern(regexp = "https?://(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)", message = "올바른 입력 형태가 아닙니다.")
	private String link;

	@NotBlank
	@Length(max = 30)
	private String writer;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime uploadDateTime;

	private PostType type;

	private CategoryName categoryName;

	private String categoryName_readOnly;

	private String thumbnailImage;

	public PostFormDto(Post post) {
		this.title = post.getTitle();
		this.content = post.getContent();
		this.beforeLink = post.getLink();
		this.link = post.getLink();
		this.writer = post.getWriter();
		this.type = post.getType();
		this.categoryName_readOnly = post.getCategory().getName().getDisplayValue();
		this.thumbnailImage = post.getThumbnailImage();
		this.uploadDateTime = post.getUploadDateTime();
	}
}

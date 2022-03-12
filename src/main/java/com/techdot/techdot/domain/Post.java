package com.techdot.techdot.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.util.Assert;

import com.techdot.techdot.dto.PostFormDto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Post {

	@Id
	@GeneratedValue
	@Column(name = "post_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Lob @Column(nullable = false)
	private String content;

	@Column(nullable = false, unique = true)
	private String link;

	@Column(nullable = false)
	private String writer;

	@Column(nullable = false)
	private LocalDateTime uploadDateTime;

	@Lob
	private String thumbnailImage;

	@Enumerated(EnumType.STRING)
	private PostType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member manager;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="category_id")
	private Category category;

	@OneToMany(mappedBy = "post")
	private List<Like> likes = new ArrayList<>();


	@Builder
	public Post(String title, String content, String writer, String link, String thumbnailImage, PostType type, Member manager, Category category,
		LocalDateTime uploadDateTime) {
		Assert.notNull(title, "post.title 값이 존재하지 않습니다.");
		Assert.notNull(content, "post.content 값이 존재하지 않습니다.");
		Assert.notNull(link, "post.link 값이 존재하지 않습니다.");
		Assert.notNull(writer, "post.writer 값이 존재하지 않습니다.");
		Assert.notNull(type, "post.type 값이 존재하지 않습니다.");
		Assert.notNull(manager, "post.member 값이 존재하지 않습니다.");
		Assert.notNull(category, "post.category 값이 존재하지 않습니다.");
		Assert.notNull(uploadDateTime, "post.uploadDateTime 값이 존재하지 않습니다.");

		this.title = title;
		this.content = content;
		this.link = link;
		this.writer = writer;
		this.type = type;
		this.thumbnailImage = thumbnailImage;
		this.uploadDateTime = uploadDateTime;
		setManager(manager);
		setCategory(category);
	}

	private void setCategory(Category category) {
		this.category = category;
	}

	private void setManager(Member manager) {
		this.manager = manager;
	}

	public void update(PostFormDto postForm) {
		this.title = postForm.getTitle();
		this.content = postForm.getContent();
		this.type = postForm.getType();
		this.link = postForm.getLink();
		this.writer = postForm.getWriter();
		this.thumbnailImage = postForm.getThumbnailImage();
		this.uploadDateTime = postForm.getUploadDateTime();
	}

	public boolean isManager(Member member) {
		return manager.equals(member);
	}
}

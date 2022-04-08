package com.techdot.techdot.modules.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.util.Assert;

import com.techdot.techdot.infra.BaseEntity;
import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.like.Like;
import com.techdot.techdot.modules.post.dto.PostFormDto;
import com.techdot.techdot.modules.member.Member;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Lob
	@Column(nullable = false)
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
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "post")
	private List<Like> likes = new ArrayList<>();

	@Builder
	public Post(final String title, final String content, final String writer, final String link,
		final String thumbnailImage, final PostType type, final Member manager, final Category category,
		final LocalDateTime uploadDateTime) {
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
		createDateTime();
	}

	private void setCategory(final Category category) {
		this.category = category;
	}

	private void setManager(final Member manager) {
		this.manager = manager;
	}

	public void update(final PostFormDto postForm, final Category category) {
		this.title = postForm.getTitle();
		this.content = postForm.getContent();
		this.type = postForm.getType();
		this.link = postForm.getLink();
		this.writer = postForm.getWriter();
		this.thumbnailImage = postForm.getThumbnailImage();
		this.uploadDateTime = postForm.getUploadDateTime();
		updateCategory(category);
		updateDateTime();
	}

	private void updateCategory(final Category category){
		this.category = category;
	}


	public boolean isManager(final Member member) {
		return manager.equals(member);
	}
}

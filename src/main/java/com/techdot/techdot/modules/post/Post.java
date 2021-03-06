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

import com.techdot.techdot.infra.domain.BaseEntity;
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

	private String thumbnailImageUrl;

	@Enumerated(EnumType.STRING)
	private PostType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member manager;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
	private List<Like> likes = new ArrayList<>();

	@Builder
	public Post(final String title, final String content, final String writer, final String link,
		final String thumbnailImageUrl, final PostType type, final Member manager, final Category category,
		final LocalDateTime uploadDateTime) {
		Assert.notNull(title, "post.title ?????? ???????????? ????????????.");
		Assert.notNull(content, "post.content ?????? ???????????? ????????????.");
		Assert.notNull(link, "post.link ?????? ???????????? ????????????.");
		Assert.notNull(writer, "post.writer ?????? ???????????? ????????????.");
		Assert.notNull(type, "post.type ?????? ???????????? ????????????.");
		Assert.notNull(manager, "post.member ?????? ???????????? ????????????.");
		Assert.notNull(uploadDateTime, "post.uploadDateTime ?????? ???????????? ????????????.");

		this.title = title;
		this.content = content;
		this.link = link;
		this.writer = writer;
		this.type = type;
		this.thumbnailImageUrl = thumbnailImageUrl;
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

	public void setImageUrl(String thumbnailImageUrl) {
		this.thumbnailImageUrl = thumbnailImageUrl;
	}

	public void update(final PostFormDto postForm, final Category category) {
		this.title = postForm.getTitle();
		this.content = postForm.getContent();
		this.type = postForm.getType();
		this.link = postForm.getLink();
		this.writer = postForm.getWriter();
		this.uploadDateTime = postForm.getUploadDateTime();
		updateCategory(category);
		updateDateTime();
	}

	private void updateCategory(final Category category) {
		this.category = category;
	}

	public boolean isManager(final Member member) {
		return manager.equals(member);
	}

}

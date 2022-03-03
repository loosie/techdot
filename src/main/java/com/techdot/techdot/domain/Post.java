package com.techdot.techdot.domain;

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

import org.springframework.util.Assert;

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

	@Column(nullable = false)
	private String content;

	@Column(nullable = false, unique = true)
	private String link;

	@Lob
	private String thumbnailImage;

	@Enumerated(EnumType.STRING)
	private PostType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public Post(String title, String content, String link, String thumbnailImage, PostType type, Member member) {
		Assert.notNull(title, "post.title 값이 존재하지 않습니다.");
		Assert.notNull(link, "post.link 값이 존재하지 않습니다.");
		Assert.notNull(content, "post.content 값이 존재하지 않습니다.");
		Assert.notNull(member, "post.member 값이 존재하지 않습니다.");
		this.title = title;
		this.content = content;
		this.link = link;
		this.type = type;
		this.thumbnailImage = thumbnailImage;
		addMember(member);
	}

	private void addMember(Member member) {
		this.member = member;
		if (!member.getPosts().contains(this)) {
			member.getPosts().add(this);
		}
	}

}

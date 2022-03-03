package com.techdot.techdot.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter @EqualsAndHashCode(of ="id")
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

	@Enumerated(EnumType.STRING)
	private PostType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public Post(Long id, String title, String content, String link, PostType type) {
		this.title = title;
		this.content = content;
		this.link = link;
		this.type = type;
	}

	@Builder(builderClassName = "ByMemberBuilder", builderMethodName = "ByMemberBuilder")
	public Post(Member member){
		this.member = member;
	}
}

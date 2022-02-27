package com.techdot.techdot.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter @EqualsAndHashCode(of ="id")
public class Member {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String email;

	@Column(unique = true)
	private String nickname;

	private String password;

	private String emailVerified;

	private String emailCheckToken;

	@Lob
	private String profileImage;

	private Boolean termsCheck;

	@Builder
	public Member(Long id, String email, String nickname, String password, String emailVerified,
		String emailCheckToken, String profileImage, Boolean termsCheck) {
		this.id = id;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.emailVerified = emailVerified;
		this.emailCheckToken = emailCheckToken;
		this.profileImage = profileImage;
		this.termsCheck = termsCheck;
	}

	public void generateEmailCheckToken() {
		this.emailCheckToken = UUID.randomUUID().toString();
	}
}

package com.techdot.techdot.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Entity
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


}

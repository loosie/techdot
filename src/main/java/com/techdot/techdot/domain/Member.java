package com.techdot.techdot.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.springframework.util.Assert;

import com.techdot.techdot.dto.ProfileFormDto;

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
	@Column(name = "member_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Column(nullable = false)
	private String password;

	private String bio;

	@Column(nullable = false)
	private Boolean emailVerified;

	private String emailCheckToken;

	private LocalDateTime emailCheckTokenSendAt;

	private Integer emailSendTime;

	@Lob
	private String profileImage;

	@Column(nullable = false)
	private Boolean termsCheck;

	@OneToMany(mappedBy = "member")
	private List<Post> posts = new ArrayList<>();

	@Builder
	public Member(Long id, String email, String nickname, String password, String bio, Boolean emailVerified,
		String emailCheckToken, LocalDateTime emailCheckTokenSendAt, Integer emailSendTime, String profileImage,
		Boolean termsCheck) {
		this.id = id;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.bio = bio;
		this.emailVerified = false;
		this.emailCheckToken = emailCheckToken;
		this.emailCheckTokenSendAt = emailCheckTokenSendAt;
		this.emailSendTime = emailSendTime;
		this.profileImage = profileImage;
		this.termsCheck = termsCheck;
	}

	public void generateEmailCheckToken() {
		this.emailCheckToken = UUID.randomUUID().toString();
		this.emailCheckTokenSendAt = LocalDateTime.now();
		this.emailSendTime = 1;
	}

	public void updateEmailCheckToken() {
		this.emailCheckToken = UUID.randomUUID().toString();
	}

	public void completeEmailVerified() {
		this.emailVerified = true;
	}

	public boolean isSameToken(String token) {
		return this.emailCheckToken.equals(token);
	}

	public boolean canSendConfirmEmail() {
		// 5초에 1번씩 총 5회 전송 가능
		if(emailSendTime < 5 && this.emailCheckTokenSendAt.isBefore(LocalDateTime.now().minusSeconds(5))){
			this.emailCheckTokenSendAt = LocalDateTime.now();
			this.emailSendTime += 1;
			return true;
		}

		// 5회 경과시 3분 지나야 재전송 가능
		if(this.emailCheckTokenSendAt.isBefore(LocalDateTime.now().minusMinutes(3))){
			this.emailCheckTokenSendAt = LocalDateTime.now();
			this.emailSendTime = 1;
			return true;
		}

		return false;
	}

	public void updateProfile(ProfileFormDto profileForm) {
		this.nickname = profileForm.getNewNickname();
		this.bio = profileForm.getBio();
		this.profileImage = profileForm.getProfileImage();
	}

	public void updatePassword(String newPassword) {
		this.password = newPassword;
	}

	public boolean isValidToken(String token) {
		return this.emailCheckToken.equals(token);
	}


}

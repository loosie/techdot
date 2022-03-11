package com.techdot.techdot.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

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

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Set<Role> roles = new HashSet<>();

	@Lob
	private String profileImage;

	@Column(nullable = false)
	private Boolean termsCheck;


	@Builder
	public Member(String email, String nickname, String password, Boolean emailVerified, Boolean termsCheck) {
		Assert.notNull(email, "member.email 값이 존재하지 않습니다.");
		Assert.notNull(nickname, "member.nickname 값이 존재하지 않습니다.");
		Assert.notNull(password, "member.password 값이 존재하지 않습니다.");
		Assert.notNull(emailVerified, "member.emailVerified 값이 존재하지 않습니다.");
		Assert.isTrue(termsCheck, "member.termsCheck 값이 올바르지 않습니다.");

		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.emailVerified = emailVerified;
		this.roles.add(Role.USER);
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
		updateRoleToMember();
		this.emailVerified = true;
	}

	private void updateRoleToMember(){
		this.roles.add(Role.MEMBER);
	}

	public Set<Role> getRoles() {
		return new HashSet<>(this.roles);
	}

	// TODO; 임시용 role 주입 함수
	public void addRole(Role... role){
		for (Role r : role) {
			this.roles.add(r);
		}
	}

	public boolean canSendConfirmEmail() {
		// 5초에 1번씩 총 5회 전송 가능
		if(emailSendTime == 1 ||
			(emailSendTime < 5 && this.emailCheckTokenSendAt.isBefore(LocalDateTime.now().minusSeconds(5)))){
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

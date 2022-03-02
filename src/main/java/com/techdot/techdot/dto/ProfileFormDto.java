package com.techdot.techdot.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.techdot.techdot.domain.Member;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileFormDto {

	private String curNickname;

	@NotBlank
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣A-z0-9]{3,20}$", message = "공백없이 문자와 숫자로만 3자 이상 20자 이내로 입력이 가능합니다.")
	@Length(min =3, max=20)
	private String newNickname;

	@Length(max = 50)
	private String bio;

	private String profileImage;

	public ProfileFormDto(Member member) {
		this.curNickname = member.getNickname();
		this.newNickname = member.getNickname();
		this.bio = member.getBio();
		this.profileImage = member.getProfileImage();
	}
}

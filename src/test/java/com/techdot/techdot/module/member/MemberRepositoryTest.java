package com.techdot.techdot.module.member;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	private Member member;
	@BeforeEach
	void setUp(){
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
	}

	@DisplayName("멤버 생성하기 - 성공")
	@Test
	void member_create_success() {
		// given
		Member saveMember = memberRepository.save(member);

		// when, then
		assertEquals(saveMember.getEmail(), member.getEmail());
		assertEquals(saveMember.getNickname(), member.getNickname());
	}

	@DisplayName("이메일로 멤버 존재여부 확인하기")
	@Test
	void member_isExistedByEmail(){
		// given
		memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.existsByEmail(member.getEmail()));
	}

	@DisplayName("닉네임으로 멤버 존재여부 확인하기")
	@Test
	void member_isExistedByNickname(){
		// given
		 memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.existsByNickname(member.getNickname()));
	}

	@DisplayName("이메일로 멤버 찾기")
	@Test
	void member_findByEmail(){
		// given
		memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.findByEmail(member.getEmail()).isPresent());
	}

	@DisplayName("닉네임로 멤버 찾기")
	@Test
	void member_findByNickname(){
		// given
		memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.findByNickname(member.getNickname()).isPresent());
	}
}
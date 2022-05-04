package com.techdot.techdot.modules.member;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.TCDataJpaTest;

@TCDataJpaTest
class MemberRepositoryTest extends AbstractContainerBaseTest {

	@Autowired
	private MemberRepository memberRepository;

	private Member member;
	@BeforeEach
	void setUp(){
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
	}

	@DisplayName("회원 생성하기")
	@Test
	void memberCreate_Success() {
		// given
		Member saveMember = memberRepository.save(member);

		// when, then
		assertEquals(saveMember.getEmail(), member.getEmail());
		assertEquals(saveMember.getNickname(), member.getNickname());
	}

	@DisplayName("이메일로 회원 정보 존재하는지 조회하기")
	@Test
	void memberExistsByEmail_Success(){
		// given
		memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.existsByEmail(member.getEmail()));
	}

	@DisplayName("닉네임으로 회원 정보 존재하는지 조회하기")
	@Test
	void memberExistsByNickname_Success(){
		// given
		 memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.existsByNickname(member.getNickname()));
	}

	@DisplayName("이메일로 회원 정보 조회하기")
	@Test
	void memberFindByEmail_Success(){
		// given
		memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.findByEmail(member.getEmail()).isPresent());
	}

	@DisplayName("닉네임로 회원 정보 조회하기")
	@Test
	void memberFindByNickname_Success(){
		// given
		memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.findByNickname(member.getNickname()).isPresent());
	}

	@DisplayName("회원 정보 삭제하기")
	@Test
	void memberDelete_Success(){
		// when
		memberRepository.delete(member);

		// then
		assertTrue(memberRepository.findByEmail(member.getEmail()).isEmpty());
	}

}
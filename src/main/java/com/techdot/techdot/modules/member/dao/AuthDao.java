package com.techdot.techdot.modules.member.dao;

import static com.techdot.techdot.infra.util.KeyGenerator.*;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.techdot.techdot.infra.util.KeyGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 이메일 토큰
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class AuthDao {

	public enum TokenType {
		EMAIL, LOGIN
	}

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * AuthToken 생성 후 캐시에 저장. 만약 캐시에 이미 존재하면 해당 토큰 반환
	 * tokenType: EMAIL, LOGIN
	 */
	public String saveAndGetAuthToken(Long memberId, String token, TokenType tokenType) {
		String key = generateAuthTokenKey(memberId, tokenType);

		// 이미 존재하면 해당 토큰 반환
		String findToken = getAuthTokenByMemberId(memberId, tokenType);
		if (findToken != null) {
			return findToken;
		}

		redisTemplate.opsForValue().set(key, token);
		redisTemplate.expire(key, 5, TimeUnit.MINUTES);
		log.info("member {} - {} token save to cache : {}", memberId, tokenType, token);
		return token;
	}

	/**
	 * 캐시에 저장된 토큰 가져오기
	 * tokenType: EMAIL, LOGIN
	 */
	public String getAuthTokenByMemberId(Long memberId, TokenType tokenType) {
		String key = generateAuthTokenKey(memberId, tokenType);

		String token = (String)redisTemplate.opsForValue().get(key);
		log.info("get member {} email token from cache : {} ", memberId, token);

		return token;
	}

	private String generateAuthTokenKey(Long memberId, TokenType tokenType) {
		if (tokenType.equals(TokenType.EMAIL)) {
			return generateEmailCheckTokenKey(memberId);
		}

		if (tokenType.equals(TokenType.LOGIN)) {
			return generateLoginTokenKey(memberId);
		}
		throw new IllegalArgumentException("잘못된 토큰 타입입니다.");
	}

}

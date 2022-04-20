package com.techdot.techdot.infra.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private int port;

	/**
        Lettuce: Multi-Thread 에서 Thread-Safe한 Redis 클라이언트로 netty에 의해 관리된다.
                 Sentinel, Cluster, Redis data model 같은 고급 기능들을 지원하며
                 비동기 방식으로 요청하기에 TPS/CPU/Connection 개수와 응답속도 등 전 분야에서 Jedis 보다 뛰어나다.
                 스프링 부트의 기본 의존성은 현재 Lettuce로 되어있다.

        Jedis  : Multi-Thread 에서 Thread-unsafe 하며 Connection pool을 이용해 멀티쓰레드 환경을 구성한다.
                 Jedis 인스턴스와 연결할 때마다 Connection pool을 불러오고 스레드 갯수가
                 늘어난다면 시간이 상당히 소요될 수 있다.

        host, port를 명시적으로 주입하는 이유는 Test 코드 실행시 컨테이너 port가 매핑되기 위함이다.
     */
	@Bean
	public RedisConnectionFactory redisConnectionFactory(){
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	/**
        RedisTemplate: Redis data access code를 간소화 하기 위해 제공되는 클래스이다.
                       주어진 객체들을 자동으로 직렬화/역직렬화 하며 binary 데이터를 Redis에 저장한다.
                       기본설정은 JdkSerializationRedisSerializer 이다.

        StringRedisSerializer: binary 데이터로 저장되기 때문에 이를 String 으로 변환시켜주며(반대로도 가능) UTF-8 인코딩 방식을 사용한다.
        GenericJackson2JsonRedisSerializer: Jackson2JsonRedisSerializer의 범용적인 타입으로, 타입을 명시하지 않아도 여러 객체를 json타입으로 직렬화/역직렬화를 수행한다.
     */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
		GenericJackson2JsonRedisSerializer genericJackson2JsonSerializer = new GenericJackson2JsonRedisSerializer();
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(genericJackson2JsonSerializer);
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(genericJackson2JsonSerializer);
		return redisTemplate;
	}

	@Bean
	public ObjectMapper objectMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.registerModules(new JavaTimeModule(), new Jdk8Module());
		return mapper;
	}

	/**
        Redis Cache 적용을 위한 RedisCacheManager 설정
	 	- RedisTemplate이 아닌 @Cacheable, @CacheEvict, @CachePut 등에 적용되는 설정
     */
	@Bean
	public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper){
		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
			.disableCachingNullValues()
			.entryTtl(Duration.ofSeconds(60))
			.serializeKeysWith(
				RedisSerializationContext.SerializationPair
				.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

		return RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(configuration)
			.build();

	}

}

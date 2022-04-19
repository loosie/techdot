package com.techdot.techdot.infra;

import org.springframework.data.redis.connection.RedisServer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

import io.lettuce.core.RedisClient;

public abstract class AbstractContainerBaseTest {
	static final String MYSQL_IMAGE = "mysql:8";
	static final MySQLContainer MYSQL_CONTAINER;
	// static final GenericContainer REDIS_CONTAINER;
	// public static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379); // 모든 테스트에 한 컨테이너로 테스트
	static final GenericContainer redis = new GenericContainer<>("redis:6-alpine")
		.withExposedPorts(6379)
		.withReuse(true);
	static {
		MYSQL_CONTAINER = new MySQLContainer(MYSQL_IMAGE);
		MYSQL_CONTAINER.start();
		redis.start();
		// REDIS_CONTAINER = new GenericContainer(DockerImageName.parse("redis:6.2.6-alpine")).withExposedPorts(6379); // 각 테스트에 새 컨테이너로 테스트
		// REDIS_CONTAINER.start();
	}

	@DynamicPropertySource
	public static void overrideProps(DynamicPropertyRegistry registry){
		System.out.println("!!!!!!!! test host: " + redis.getHost());
		System.out.println("!!!!!!!! test port: " + redis.getMappedPort(6379));
		registry.add("spring.redis.host", redis::getHost);
		registry.add("spring.redis.port", () -> ""+redis.getMappedPort(6379));
	}


	// public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	//
	// 	static GenericContainer redis = new GenericContainer<>("redis:6-alpine")
	// 		.withExposedPorts(6379)
	// 		.withReuse(true);
	//
	// 	@Override
	// 	public void initialize(ConfigurableApplicationContext context) {
	// 		// Start container
	// 		redis.start();
	//
	// 		// Override Redis configuration
	// 		String redisContainerIP = "spring.redis.host=" + redis.getContainerIpAddress();
	// 		String redisContainerPort = "spring.redis.port=" + redis.getMappedPort(6379); // <- This is how you get the random port.
	// 		TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context,  redisContainerIP, redisContainerPort); // <- This is how you override the configuration in runtime.
	// 	}
	// }
}

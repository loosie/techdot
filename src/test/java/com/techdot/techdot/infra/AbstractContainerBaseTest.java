package com.techdot.techdot.infra;

import static com.techdot.techdot.infra.Constant.*;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;


public abstract class AbstractContainerBaseTest {
	static final GenericContainer MYSQL_CONTAINER;
	static final GenericContainer REDIS_CONTAINER;

	static {
		MYSQL_CONTAINER = new MySQLContainer(MYSQL_IMAGE)
			.withReuse(true);
		MYSQL_CONTAINER.start();

		REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
			.withExposedPorts(6379)
			.withReuse(true);
		REDIS_CONTAINER.start();
	}

	@DynamicPropertySource
	public static void overrideProps(DynamicPropertyRegistry registry){
		registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
		registry.add("spring.redis.port", () -> ""+REDIS_CONTAINER.getMappedPort(6379));
	}

}

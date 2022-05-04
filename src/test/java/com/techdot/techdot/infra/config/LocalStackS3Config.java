package com.techdot.techdot.infra.config;

import static com.techdot.techdot.infra.Constant.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@TestConfiguration
public class LocalStackS3Config {
	private static final LocalStackContainer LOCAL_STACK_CONTAINER = new LocalStackContainer(LOCAL_STACK_IMAGE)
		.withServices(S3)
		.withReuse(true);

	// GenericContainer start(), stop() 메서드로 생명주기 설정
	@Bean(initMethod = "start", destroyMethod = "stop")
	public LocalStackContainer localStackContainer(){
		return LOCAL_STACK_CONTAINER;
	}

	@Bean
	public AmazonS3 amazonS3(LocalStackContainer localStackContainer){
		return AmazonS3ClientBuilder
			.standard()
			.withEndpointConfiguration(localStackContainer.getEndpointConfiguration(S3))
			.withCredentials(localStackContainer.getDefaultCredentialsProvider())
			.build();
	}
}
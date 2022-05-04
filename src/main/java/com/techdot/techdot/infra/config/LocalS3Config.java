package com.techdot.techdot.infra.config;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Profile({"local", "test"})
@Configuration
public class LocalS3Config {

	@Value("${aws.s3.bucket}")
	public String bucket;

	private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack");

	// GenericContainer start(), stop() 메서드로 생명주기 설정
	@Bean(initMethod = "start", destroyMethod = "stop")
	public LocalStackContainer localStackContainer() {
		return new LocalStackContainer(LOCALSTACK_IMAGE)
			.withServices(S3);
	}

	@Bean
	public AmazonS3 amazonS3(LocalStackContainer localStackContainer) {
		AmazonS3 amazonS3 = AmazonS3ClientBuilder
			.standard()
			.withEndpointConfiguration(localStackContainer.getEndpointConfiguration(S3))
			.withCredentials(localStackContainer.getDefaultCredentialsProvider())
			.build();
		amazonS3.createBucket(bucket);
		return amazonS3;
	}

}

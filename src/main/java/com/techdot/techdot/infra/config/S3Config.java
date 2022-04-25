package com.techdot.techdot.infra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Profile({"dev", "real1", "real2"})
@Configuration
public class S3Config {

	@Autowired
	private S3Properties s3Properties;

	@Bean
	public BasicAWSCredentials awsCredentialsProvider(){
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey());
		return basicAWSCredentials;
	}

	@Bean
	public AmazonS3 amazonS3() {
		return AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(awsCredentialsProvider()))
			.build();
	}


}

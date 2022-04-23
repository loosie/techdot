package com.techdot.techdot.infra.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

	private final AmazonS3 amazonS3;

	@Value("${aws.s3.bucket}")
	public String bucket;

	/**
	 * bucket에 있는 key 삭제하기
	 */
	public void delete(String key){
		key = "static/" + key;
		if(amazonS3.doesObjectExist(bucket, key)){
			amazonS3.deleteObject(bucket, key);
			log.info("s3 버킷 이미지 삭제 완료 - {}", key);
		}
	}




}

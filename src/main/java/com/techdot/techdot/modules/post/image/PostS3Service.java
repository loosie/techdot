package com.techdot.techdot.modules.post.image;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostS3Service {

	private final AmazonS3 amazonS3;

	@Value("${aws.s3.bucket}")
	public String bucket;

	static final String FOLDER_NAME = "static/";

	/**
	 * S3에 File 업로드
	 * inputStream으로 로컬에 쓰기 작업을 거치지 않고 바로 S3에 업로드
	 * AmazonS3#putObject(String, String, InputStream, ObjectMetadata)
	 */
	public String upload(MultipartFile file) {
		String fileName = FOLDER_NAME + file.getOriginalFilename();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(MediaType.IMAGE_PNG_VALUE);
		metadata.setContentLength(file.getSize());

		try {
			amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}

		return amazonS3.getUrl(bucket, fileName).toString();
	}

	/**
	 * bucket에 있는 key 삭제하기
	 */
	public void delete(String key){
		key = FOLDER_NAME + key;
		if(amazonS3.doesObjectExist(bucket, key)){
			amazonS3.deleteObject(bucket, key);
			log.info("s3 버킷 이미지 삭제 완료 - {}", key);
		}
	}


}

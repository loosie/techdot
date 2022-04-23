package com.techdot.techdot.infra.image;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class S3Controller {

	private final AmazonS3 amazonS3;

	@Value("${aws.s3.bucket}")
	public String bucket;



	/**
	 * S3에 File 업로드
	 * inputStream으로 로컬에 쓰기 작업을 거치지 않고 바로 S3에 업로드
	 */
	@PostMapping("/api/image-upload")
	public String imageUpload(MultipartFile file) throws IOException{
		String fileName = "static/" + file.getOriginalFilename();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(MediaType.IMAGE_PNG_VALUE);
		metadata.setContentLength(file.getSize());

		amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
			.withCannedAcl(CannedAccessControlList.PublicRead));

		return amazonS3.getUrl(bucket, fileName).toString();
	}

	/**
	 * TODO: 테스트용 need to delete
	 */
	@PostMapping("/s3")
	public String upload(MultipartFile file) throws IOException{
		String fileName = "static/" + file.getOriginalFilename();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(MediaType.IMAGE_PNG_VALUE);
		metadata.setContentLength(file.getSize());

		amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
			.withCannedAcl(CannedAccessControlList.PublicRead));

		return amazonS3.getUrl(bucket, fileName).toString();
	}

	/**
	 * TODO: 테스트용 need to delete
	 */
	@GetMapping("/s3")
	public void getImage() throws IOException{
		S3Object object = amazonS3.getObject(new GetObjectRequest(bucket, "static/image1.png"));

		S3ObjectInputStream ois = object.getObjectContent();
		System.out.println(ois);

		// br = new BufferedReader(new InputStreamReader(ois, "UTF-8"));
		// String line;
		// while ((line = br.readLine()) != null) {
		// 	System.out.println(line);
		// }
	}




}

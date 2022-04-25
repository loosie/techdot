package com.techdot.techdot.modules.post.image;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.techdot.techdot.modules.post.Post;
import com.techdot.techdot.modules.post.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Profile({"dev", "real1", "real2"})
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RealPostS3Service implements PostS3Service{

	private final AmazonS3 amazonS3;
	private final PostRepository postRepository;

	@Value("${aws.s3.bucket}")
	public String BUCKET;

	@Value("${cdn.public-url}")
	public String PUBLIC_URL;

	static final String FOLDER_NAME = "static/";

	/**
	 * S3에 File 업로드
	 * inputStream으로 로컬에 쓰기 작업을 거치지 않고 바로 S3에 업로드
	 * AmazonS3#putObject(String, String, InputStream, ObjectMetadata)
	 */
	@Override
	public String upload(Long id, MultipartFile file) {
		String fileName = file.getOriginalFilename();

		int i = file.getOriginalFilename().indexOf("static/");
		if(i != -1) {
			fileName = file.getOriginalFilename().substring(i + 7);
		}
		String fileStorageName = FOLDER_NAME + fileName;

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(MediaType.IMAGE_PNG_VALUE);
		metadata.setContentLength(file.getSize());

		try {
			amazonS3.putObject(new PutObjectRequest(BUCKET, fileStorageName, file.getInputStream(), metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}

		return savePostImageUrl(id, fileName);
	}

	private String savePostImageUrl(Long id, String fileName) {
		Post post = postRepository.getById(id);
		String url = "";
		if(post.getThumbnailImageUrl() == null || post.getThumbnailImageUrl().isEmpty()){
			url = PUBLIC_URL + FOLDER_NAME + fileName;
			post.setImageUrl(url);
			postRepository.save(post);
		} else {
			url = post.getThumbnailImageUrl();
		}

		log.info("s3 버킷 이미지 등록 완료 - {}", url);
		return url;
	}

	/**
	 * bucket에 있는 key 삭제하기
	 */
	@Override
	public void delete(String key){
		key = FOLDER_NAME + key;
		if(amazonS3.doesObjectExist(BUCKET, key)){
			amazonS3.deleteObject(BUCKET, key);
			log.info("s3 버킷 이미지 삭제 완료 - {}", key);
		}
	}


}

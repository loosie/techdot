package com.techdot.techdot.modules.post.image;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostS3Controller {

	private final PostS3Service s3Service;

	/**
	 * S3에 File 업로드하는 API
	 */
	@PostMapping("/api/post/{id}/image-upload")
	public ResponseEntity<String> imageUpload(@PathVariable Long id, MultipartFile file) {
		return ResponseEntity.ok(s3Service.upload(id, file));
	}

}

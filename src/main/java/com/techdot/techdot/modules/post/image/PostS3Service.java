package com.techdot.techdot.modules.post.image;

import org.springframework.web.multipart.MultipartFile;

public interface PostS3Service {
	String upload(Long id, MultipartFile file);

	void delete(String key);
}

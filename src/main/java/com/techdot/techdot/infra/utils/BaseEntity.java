package com.techdot.techdot.infra.utils;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {
	private LocalDateTime createdDateTime;
	private LocalDateTime lastModifiedTime;

	public LocalDateTime getCreatedDateTime() {
		return createdDateTime;
	}

	public void createDateTime() {
		this.createdDateTime = LocalDateTime.now();
		this.lastModifiedTime = LocalDateTime.now();
	}

	public void updateDateTime() {
		this.lastModifiedTime = LocalDateTime.now();
	}

	public LocalDateTime getLastModifiedTime() {
		return lastModifiedTime;
	}
}

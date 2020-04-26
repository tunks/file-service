package com.att.kepler.fs.dto;

import org.springframework.web.multipart.MultipartFile;
import com.att.kepler.fs.model.FileInfo;

public class FileDto extends FileInfo{
	private MultipartFile uploadedFile;

	public FileDto() {
	}

	public FileDto(String fileId) {
		super.setId(fileId);
	}

	public FileDto(String createdBy, String namespace, MultipartFile uploadedFile) {
		super.setCreatedBy(createdBy);
		this.setNamespace(namespace);
		this.uploadedFile = uploadedFile;
	}

	public MultipartFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(MultipartFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	@Override
	public String toString() {
		return "FileDto [toString()=" + super.toString() + "]";
	}
	
}

package dev.tunk.fs.dto;

import org.springframework.web.multipart.MultipartFile;

import dev.tunks.fs.model.FileInfo;

public class FileDto extends FileInfo{
	private MultipartFile uploadedFile;

	public FileDto() {
	}

	public FileDto(String fileId) {
		super.setId(fileId);
	}

	public FileDto(String createdBy, MultipartFile uploadedFile) {
		super.setCreatedBy(createdBy);
		this.uploadedFile = uploadedFile;
		if(uploadedFile != null) {
		   this.setFileName(uploadedFile.getOriginalFilename());
		}
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

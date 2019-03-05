package com.att.kepler.fs.support;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FileInfo {
	public static String DEFAULT_NAMESPACE = "com.att.kepler";
	public static int DEFAULT_VERSION = 1;
    private String fileName;
    private String fileType;
    private String fileNamespace;
    private String createdBy;
    private int fileVersion = DEFAULT_VERSION;
	
	public int getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(int fileVersion) {
		this.fileVersion = fileVersion;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileNamespace() {
		return (fileNamespace != null)? fileNamespace: DEFAULT_NAMESPACE;
	}

	public void setFileNamespace(String fileNamespace) {
		this.fileNamespace = fileNamespace;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public DBObject dbObject() {
		DBObject object = new BasicDBObject();
		object.put("fileName",  this.fileName);
		object.put("createdBy", this.createdBy);
		object.put("namespace", this.getFileNamespace());
		object.put("fileType",  this.fileType);
		object.put("fileVersion", this.fileVersion);
		return object;
	}

	@Override
	public String toString() {
		return "FileInfo [fileName=" + fileName + ", fileType=" + fileType + ", fileNamespace=" + fileNamespace
				+ ", createdBy=" + createdBy + "]";
	}

	public static class FileInfoBuilder{
		private FileInfo info;
		
		public FileInfoBuilder() {
			info = new FileInfo();
		}
		
		public FileInfoBuilder setFileName(String fileName) {
			this.info.setFileName(fileName);
			return this;
		}

		public FileInfoBuilder setFileType(String fileType) {
			this.info.setFileType(fileType);
			return this;
		}
		
		public FileInfoBuilder setFileNamespace(String fileNamespace) {
			this.info.setFileNamespace(fileNamespace);
			return this;
		}
		
		public FileInfoBuilder setCreatedBy(String createdBy) {
			this.info.setCreatedBy(createdBy);
			return this;
		}
		public FileInfoBuilder setFileVersion(int fileVersion) {
			this.info.setFileVersion(fileVersion);
			return this;
		}
		
		public FileInfo build() {
			return this.info;
		}
	}
}

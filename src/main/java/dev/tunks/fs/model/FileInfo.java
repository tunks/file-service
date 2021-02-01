package dev.tunks.fs.model;

import java.net.URLConnection;

import javax.activation.MimetypesFileTypeMap;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.MimeMappings.Mapping;
import org.springframework.http.MediaType;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import dev.tunk.fs.dto.FileDto;

public class FileInfo {
	public static int DEFAULT_VERSION = 1;
	private String id;
	private String fileName;
	private String fileType;
	private String createdBy;
	private String namespace;
	private String resourceId;
	private String resourceType;
	private int fileVersion = DEFAULT_VERSION;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public DBObject dbObject() {
		DBObject object = new BasicDBObject();
		//object.put("fileName", this.fileName);
		object.put("createdBy", this.createdBy);
		object.put("fileType", this.fileType);
		object.put("fileVersion", this.fileVersion);
		object.put("namespace", this.namespace);
		object.put("resourceId", this.resourceId);
		object.put("resourceType", this.resourceType);
		return object;
	}

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", fileName=" + fileName + ", fileType=" + fileType + ", createdBy=" + createdBy
				+ ", namespace=" + namespace + ", resourceId=" + resourceId + ", resourceType=" + resourceType
				+ ", fileVersion=" + fileVersion + "]";
	}


	public static class FileInfoBuilder {
		private FileInfo info;

		public FileInfoBuilder() {
			info = new FileInfo();
		}

		public FileInfoBuilder(FileDto fileDto) {
			 this();
			 this.info = fileDto;
			 if(fileDto.getUploadedFile() != null) {
				 String filename = fileDto.getUploadedFile().getOriginalFilename();
				 String fileType = getFileMimeType(filename);
			     this.setFileName(filename);
			     this.setFileType(fileType);
			 }
		}

		public FileInfoBuilder setId(String id) {
			this.info.setId(id);
			return this;
		}

		public FileInfoBuilder setFileName(String fileName) {
			this.info.setFileName(fileName);
			return this;
		}

		public FileInfoBuilder setFileType(String fileType) {
			this.info.setFileType(fileType);
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

		public FileInfo getInfo() {
			return info;
		}

		public void setInfo(FileInfo info) {
			this.info = info;
		}

		private String getFileMimeType(String filename) {
			try {
			  MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
				String mimeType = URLConnection.guessContentTypeFromName(filename);

			  
			  //String mimeType = fileTypeMap.getContentType(filename);
			  System.out.println("filename "+filename +", mimeType: "+mimeType);
			  return mimeType;
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			return MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		public FileInfo build() {
			return this.info;
		}
	}
}

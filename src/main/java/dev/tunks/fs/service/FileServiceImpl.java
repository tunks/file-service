package dev.tunks.fs.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;
import dev.tunk.fs.dto.FileDto;
import dev.tunk.fs.repositories.FileInfoRepository;
import dev.tunks.fs.exceptions.FileException;
import dev.tunks.fs.model.FileInfo;
import dev.tunks.fs.support.FileResource;

@Service("fileService")
public class FileServiceImpl implements FileService<FileInfo>,InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

	@Value("${connectionUrl}")
	private String connectionUrl;
	
	@Value("${endpoint}")
	private String endpoint;
	
	@Value("${sasToken}")
	private String sasToken;
	
	@Value("${containerName}")
	private String containerName;
	
	@javax.annotation.Resource
	private FileInfoRepository fileInfoRepository;
	
	private BlobContainerClient blobContainerClient;

	@Override
	public List<FileInfo> findAll(FileDto fileDto) {
		List<FileInfo> files = new ArrayList<FileInfo>();
		try {
			Iterator<FileInfo> fileInfos = fileInfoRepository.findAll().iterator();
			while(fileInfos.hasNext()) {
				files.add(fileInfos.next());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return files;
	}

	@Override
	public Optional<FileResource> getFile(FileDto fileDto) throws FileException {
		try {
			Optional<FileInfo> fileInfoOpt = fileInfoRepository.findById(fileDto.getId());
			if(fileInfoOpt.isPresent()) {
				FileInfo fileInfo = fileInfoOpt.get();
				logger.info("Get file: "+fileInfo);
		    	BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(fileInfo.getFileName()).getBlockBlobClient();
		    	ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		    	blockBlobClient.download(outstream);
				FileResource fileResource = new FileResource();
				fileResource.setFileName(fileInfo.getFileName());
				fileResource.setFileType(fileInfo.getFileType());
				Resource resource = new ByteArrayResource(outstream.toByteArray());
				fileResource.setResource(resource);
				return Optional.ofNullable(fileResource);
			}
			else {
				return Optional.empty();
			}
		} catch (IllegalStateException ex) {
			logger.error(ex.getMessage());
			throw new FileException(ex.getMessage());
		}
	}

	@Override
	public void save(FileDto fileDto) throws FileException {
	    try
	    {
	    	BlobClient blockBlobClient = blobContainerClient.getBlobClient(fileDto.getFileName());
	    	MultipartFile multipartFile = fileDto.getUploadedFile();
	        blockBlobClient.upload(multipartFile.getInputStream(), multipartFile.getResource().contentLength());
	        FileInfo fileInfo = new FileInfo();
	        fileInfo.setId(UUID.randomUUID().toString());
	        fileInfo.setFileName(fileDto.getFileName());
	        fileInfo.setFileType(fileDto.getFileType());
	        fileInfo.setCreatedBy(fileDto.getCreatedBy());
	        if(fileInfo.getFileType() == null) {
	           fileInfo.setFileType(fileMimeType(fileInfo.getFileName()));
	        }
	        logger.info("Save file: "+fileInfo);
	        fileInfoRepository.save(fileInfo);
		} catch (IOException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
		blobContainerClient = new BlobContainerClientBuilder()
							     .sasToken(sasToken)
								 .connectionString(connectionUrl)
							     .containerName(containerName)
							     .buildClient();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	private String fileMimeType(String filename) {
	    MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();	
	    return fileTypeMap.getContentType(filename);
	}
}

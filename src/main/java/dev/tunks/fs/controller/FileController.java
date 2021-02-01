package dev.tunks.fs.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.tunk.fs.dto.FileDto;
import dev.tunks.fs.exceptions.FileException;
import dev.tunks.fs.model.FileInfo;
import dev.tunks.fs.service.FileService;
import dev.tunks.fs.support.FileResource;

@RestController
@RequestMapping("/api/files")
public class FileController {
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private FileService<InputStream> fileService;
	
	@PostMapping()
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file , 
			                        @RequestParam(name="createdby", required= false) String createdBy, 
			                        @RequestParam(name="ns" , required= false) String namespace,
			                        @RequestParam(name="resourceId") String resourceId,
			                        @RequestParam(name="resourceType") String resourceType) {	
		try {
			FileDto dto = new FileDto(createdBy,namespace, file);
			dto.setResourceId(resourceId);
			dto.setResourceType(resourceType);
			dto.setNamespace(namespace);
			fileService.save(dto);
			return ResponseEntity.ok().body("Success");
		} catch (FileException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@GetMapping(  produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<FileInfo>> getFiles(@RequestParam(name = "ns", required=false) String namespace,
			                                       @RequestParam(name = "resourceId",  required=false) String resourceId,
			                                       @RequestParam(name = "resourceType", required=false) String resourceType,
			                                       @RequestParam(name = "fileName", required=false) String fileName) {
		    FileDto fileDto = new FileDto();
		    fileDto.setNamespace(namespace);
		    fileDto.setResourceId(resourceId);
		    fileDto.setResourceType(resourceType);
		    fileDto.setFileName(fileName);
			List<FileInfo> files = fileService.findAll(fileDto);
			return ResponseEntity.ok(files);
	}
	
	@GetMapping(value="/download/{id}")
	public ResponseEntity<?> downloadById( @PathVariable(name = "id") String fileId){
		try {
		   FileDto fileDto = new FileDto(fileId);
		   System.out.println("Download: "+fileDto);
		   Optional<FileResource> fileResource = fileService.getFile(fileDto);
		   if(fileResource.isPresent()) {
			  FileResource resource = fileResource.get();
			  String fileType= resource.getFileType();
			  System.out.println("fileType :"+fileType);
		      return ResponseEntity.ok()
				              .contentType(MediaType.valueOf(resource.getFileType()))
				              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFileName() + "\"")
				              .body(new InputStreamResource(resource.getResource().getInputStream()));
		  }
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
		}
		catch(FileException | IOException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}	
	}
	
	@GetMapping(value="/rs/download")
	public ResponseEntity<?> downloadByResourceInfo( @RequestParam(name = "resourceId",  required=false) String resourceId,
								               @RequestParam(name = "resourceType", required=false) String resourceType,
								               @RequestParam(name = "ns", required=false) String namespace){
		try {
		   FileDto fileDto = new FileDto();
		   fileDto.setResourceId(resourceId);
		   fileDto.setResourceType(resourceType);
		   fileDto.setNamespace(namespace);
		   System.out.println("Download 2 : "+fileDto);
		   Optional<FileResource> fileResource = fileService.getFile(fileDto);
		   if(fileResource.isPresent()) {
			  FileResource resource = fileResource.get();
			  String fileType= resource.getFileType();
			  System.out.println("fileType :"+fileType);
		      return ResponseEntity.ok()
				              .contentType(MediaType.valueOf(resource.getFileType()))
				              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFileName() + "\"")
				              .body(new InputStreamResource(resource.getResource().getInputStream()));
		  }
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
		}
		catch(FileException | IOException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}	
	}
	
	
	@GetMapping(value="/view/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> viewById( @PathVariable(name = "id") String fileId){
		try {
		   FileDto fileDto = new FileDto(fileId);
		   System.out.println("View: "+fileDto);
		   Optional<FileResource> fileResource = fileService.getFile(fileDto);
		   if(fileResource.isPresent()) {
			  FileResource resource = fileResource.get();
			  String fileType= resource.getFileType();
			  System.out.println("fileType :"+fileType);
			  byte[] media = IOUtils.toByteArray(resource.getResource().getInputStream());
		      return ResponseEntity.ok()
		    		  .header(HttpHeaders.CACHE_CONTROL,CacheControl.noCache().getHeaderValue())
		    		  .body(media);
		  }
		   return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found".getBytes());
		}
		catch(FileException | IOException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage().getBytes());
		}	
	}
	
	@GetMapping(value="/rs/view/", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> viewByResourceInfo( @RequestParam(name = "resourceId",  required=false) String resourceId,
								                      @RequestParam(name = "resourceType", required=false) String resourceType,
								                      @RequestParam(name = "ns", required=false) String namespace){
		try {
		   FileDto fileDto = new FileDto();
		   fileDto.setResourceId(resourceId);
		   fileDto.setResourceType(resourceType);
		   fileDto.setNamespace(namespace);
		   System.out.println("View 2 : "+fileDto);
		   Optional<FileResource> fileResource = fileService.getFile(fileDto);
		   if(fileResource.isPresent()) {
			  FileResource resource = fileResource.get();
			  String fileType= resource.getFileType();
			  System.out.println("fileType :"+fileType);
			  byte[] media = IOUtils.toByteArray(resource.getResource().getInputStream());
		      return ResponseEntity.ok()
		    		  .header(HttpHeaders.CACHE_CONTROL,CacheControl.noCache().getHeaderValue())
		    		  .body(media);
		  }
		   return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found".getBytes());
		}
		catch(FileException | IOException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage().getBytes());
		}	
	}
	

}

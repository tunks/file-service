package com.att.kepler.fs.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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

import com.att.kepler.fs.service.FileService;
import com.att.kepler.fs.support.FileInfo;

@RestController
@RequestMapping("/api/files")
public class FileController {
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private FileService<InputStream> fileService;
	
	@PostMapping
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file , 
			                        @RequestParam(name="createdby") String createdBy, 
			                        @RequestParam(name="ns", required=false)String namespace) {	
		try {
			FileInfo info = new FileInfo.FileInfoBuilder()
	                .setCreatedBy(createdBy)
	                .setFileName(file.getOriginalFilename())
	                .setFileType(file.getContentType())
	                .setFileNamespace(namespace)
	                .build();
			fileService.save(info, file.getInputStream());
			return ResponseEntity.ok().body("Success");

		} catch (IOException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}


	@GetMapping(  produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<FileInfo>> getFiles(@RequestParam("name") String filename, 
									               @RequestParam(name = "ns", required=false) String namespace,
									               @RequestParam(name = "fv", required=false) String fileVersion) {
			 FileInfo info = new FileInfo.FileInfoBuilder()
		                .setFileName(filename)
		                .setFileNamespace(namespace)
		                .build();
			 
			List<FileInfo> files = fileService.findAll(info);
			return ResponseEntity.ok(files);
	}
	
	@GetMapping(value="/download/{filename}")
	public ResponseEntity<?> download(@PathVariable String filename, 
			                           @RequestParam(name = "ns", required=false) String namespace,
			                           @RequestParam(name = "fv", required=false) String fileVersion) {
		try {
		  FileInfo info = new FileInfo.FileInfoBuilder()
                .setFileName(filename)
                .setFileNamespace(namespace)
                .build();
		  InputStream inputStream = fileService.getResource(info);
		  if(inputStream != null) {
		     return ResponseEntity.ok()
				              .contentType(MediaType.valueOf(info.getFileType()))
				              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + info.getFileName() + "\"")
				              .body(new InputStreamResource(inputStream));
		  }
		}
		catch(Exception ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}	
		return ResponseEntity.notFound().build();
	}

}

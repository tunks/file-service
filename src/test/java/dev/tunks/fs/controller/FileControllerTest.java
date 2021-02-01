package dev.tunks.fs.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import dev.tunk.fs.dto.FileDto;
import dev.tunks.fs.FileServiceApplication;
import dev.tunks.fs.model.FileInfo;
import dev.tunks.fs.service.FileService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileServiceApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class FileControllerTest {
	@Autowired
	private FileService<InputStream> fileService;
	
	@Autowired
	private MockMvc mvc;
	
	private String FILES_URL = "/api/files";

	private String resourceId = "1000";
	private String resourceType = "user";
	String fileName = "filename.txt";  

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testUspload() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, "some xml".getBytes());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(FILES_URL)
				                                              .file(file)
                                                              .param("createdby", "testUser")
                                                              .param("resourceId", resourceId)
                                                              .param("resourceType", resourceType)
                                                              .param("namespace", "user.profile"); 
		mvc.perform(requestBuilder)
		   .andExpect(status().is(200));
	}

	@Test
	public void testDownloadSuccess() throws Exception {
		String tmpFileName = RandomStringUtils.randomAlphabetic(5) + ".txt";
		MockMultipartFile file = new MockMultipartFile("file", tmpFileName, MediaType.TEXT_PLAIN_VALUE, "some xml 1".getBytes());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(FILES_URL)
				                                              .file(file)
                                                              .param("createdby", "testUser")
                                                              .param("resourceId", resourceId)
                                                              .param("resourceType", resourceType)
                                                              .param("namespace", "user.profile"); 
		 mvc.perform(requestBuilder)
		   .andExpect(status().is(200));
		
		FileDto fileDto = new FileDto();
		fileDto.setFileName(tmpFileName);
		List<FileInfo> files = fileService.findAll(fileDto);
		assertFalse(files.isEmpty());
		
		String fileId = files.get(0).getId();
		System.out.println("File ID: "+fileId);
		requestBuilder = MockMvcRequestBuilders.get(FILES_URL+"/download/{id}", fileId)
					                                              .accept(MediaType.APPLICATION_JSON);
		mvc.perform(requestBuilder)
		   .andExpect(status().isOk())
	       .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE));
		
		requestBuilder = MockMvcRequestBuilders.get(FILES_URL+"/rs/download/")
			                .accept(MediaType.APPLICATION_JSON)  
			                .queryParam("resourceId", resourceId);
			               // .param("resourceId", resourceId)
                            //.param("resourceType", resourceType);
		
			mvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE));
	}

	@Test
	public void testDownloadNotFound() throws Exception {
		  String fileId= UUID.randomUUID().toString();
		  RequestBuilder requestBuilder = MockMvcRequestBuilders.get(FILES_URL+"/download/{id}", fileId)
					                                              .accept(MediaType.APPLICATION_JSON);
		  mvc.perform(requestBuilder)
		     .andExpect(status().isNotFound());
	}

	
	@Test
	public void testViewFileSuccess() throws Exception {
		String tmpFileName = RandomStringUtils.randomAlphabetic(5) + ".txt";
		MockMultipartFile file = new MockMultipartFile("file", tmpFileName, MediaType.TEXT_PLAIN_VALUE, "some xml 1".getBytes());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(FILES_URL)
				                                              .file(file)
                                                              .param("createdby", "testUser")
                                                              .param("resourceId", resourceId)
                                                              .param("resourceType", resourceType)
                                                              .param("namespace", "user.profile"); 
		 mvc.perform(requestBuilder)
		   .andExpect(status().is(200));
		
		FileDto fileDto = new FileDto();
		fileDto.setFileName(tmpFileName);
		List<FileInfo> files = fileService.findAll(fileDto);
		assertFalse(files.isEmpty());
		
		String fileId = files.get(0).getId();
		System.out.println("File ID: "+fileId);
		requestBuilder = MockMvcRequestBuilders.get(FILES_URL+"/view/{id}", fileId)
					                                              .accept(MediaType.APPLICATION_OCTET_STREAM);
		mvc.perform(requestBuilder)
		   .andExpect(status().isOk())
	       .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
		
		requestBuilder = MockMvcRequestBuilders.get(FILES_URL+"/rs/view/")
			                .accept(MediaType.APPLICATION_OCTET_STREAM)  
			                .queryParam("resourceId", resourceId)
                            .param("resourceType", resourceType);
		
			mvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
	}
	
//	@Test
//	public void testGetFileList() throws Exception {
//		String fileName =  "filename1.txt";
//		//upload files
//		MockMultipartFile file = new MockMultipartFile("file",fileName, MediaType.TEXT_PLAIN_VALUE, "some xml".getBytes());
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(FILES_URL)
//				                                              .file(file)
//                                                              .param("createdby", "testUser");
//		mvc.perform(requestBuilder)
//		   .andExpect(status().is(200));
//		
//		//get list of files info
//		requestBuilder = MockMvcRequestBuilders.get(FILES_URL)
//                         .accept(MediaType.APPLICATION_JSON)
//                         .param("name", fileName);
//		mvc.perform(requestBuilder)
//		   .andExpect(status().isOk())
//	       .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
//	}
}

package dev.tunks.fs.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.time.Instant;
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
import dev.tunks.fs.ApplicationConfig;
import dev.tunks.fs.FileServiceApplication;
import dev.tunks.fs.model.FileInfo;
import dev.tunks.fs.service.FileService;
import dev.tunks.fs.service.FileServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FileServiceApplication.class, ApplicationConfig.class, FileServiceImpl.class})
@AutoConfigureMockMvc
@WebAppConfiguration
public class FileControllerTest {
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private FileService<FileInfo> fileService;
	
	private String FILES_URL = "/api/files";

	private String resourceId = "1000";
	private String resourceType = "user";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testUpload() throws Exception {
		String fileName = Instant.now().getEpochSecond() +".txt";
		MockMultipartFile file = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, "this is the first test data".getBytes());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(FILES_URL)
				                                              .file(file)
                                                              .param("createdby", "testUser");
		mvc.perform(requestBuilder)
		   .andExpect(status().is(200));
	}

	@Test
	public void testDownloadSuccess() throws Exception {
		String fileName = Instant.now().getEpochSecond() +".xml";
		MockMultipartFile file = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, "<data><element></element></data".getBytes());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(FILES_URL)
				                                              .file(file)
                                                              .param("createdby", "testUser") ;
		 mvc.perform(requestBuilder)
		   .andExpect(status().is(200));
		
		FileDto fileDto = new FileDto();
		fileDto.setFileName(fileName);
		List<FileInfo> files = fileService.findAll(fileDto);
		assertFalse(files.isEmpty());
		
		FileInfo info = files.get(0);
		System.out.println("File ID: "+info.getId());
		requestBuilder = MockMvcRequestBuilders.get(FILES_URL+"/download/{id}", info.getId())
					                                              .accept(MediaType.APPLICATION_JSON);
		mvc.perform(requestBuilder)
		   .andExpect(status().isOk())
	       .andExpect(content().contentType(info.getFileType()));
	}

	@Test
	public void testDownloadNotFound() throws Exception {
		  String fileId= UUID.randomUUID().toString();
		  RequestBuilder requestBuilder = MockMvcRequestBuilders.get(FILES_URL+"/download/{id}", fileId)
					                                              .accept(MediaType.APPLICATION_JSON);
		  mvc.perform(requestBuilder)
		     .andExpect(status().isNotFound());
	}
}

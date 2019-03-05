package com.att.kepler.fs.web;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.att.kepler.fs.FileServiceApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileServiceApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class FileControllerTest {
	@Autowired
	private MockMvc mvc;
	
	private String FILES_URL = "/api/files";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUspload() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "some xml".getBytes());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(FILES_URL)
				                                              .file(file)
                                                              .param("createdby", "testUser");
		mvc.perform(requestBuilder)
		   .andExpect(status().is(200));
	}

	@Test
	public void testDownloadSuccess() throws Exception {
		String fileName = "filename.txt";  
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(FILES_URL+"/download/{name}", fileName)
					                                              .accept(MediaType.APPLICATION_JSON);
		mvc.perform(requestBuilder)
		   .andExpect(status().isOk())
	       .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE));
	}

	@Test
	public void testDownloadNotFound() throws Exception {
		  String fileName = "no-filename.txt";
		  
		  RequestBuilder requestBuilder = MockMvcRequestBuilders.get(FILES_URL+"/download/{name}", fileName)
					                                              .accept(MediaType.APPLICATION_JSON);
		  mvc.perform(requestBuilder)
		     .andExpect(status().isNotFound());
	}

	
	@Test
	public void testGetFileList() throws Exception {
		String fileName =  "filename1.txt";
		//upload files
		MockMultipartFile file = new MockMultipartFile("file",fileName, MediaType.TEXT_PLAIN_VALUE, "some xml".getBytes());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(FILES_URL)
				                                              .file(file)
                                                              .param("createdby", "testUser");
		mvc.perform(requestBuilder)
		   .andExpect(status().is(200));
		
		//get list of files info
		requestBuilder = MockMvcRequestBuilders.get(FILES_URL)
                         .accept(MediaType.APPLICATION_JSON)
                         .param("name", fileName);
		mvc.perform(requestBuilder)
		   .andExpect(status().isOk())
	       .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}
}

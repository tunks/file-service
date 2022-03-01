package com.att.kepler.fs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Main application class 
 * Updated my class
 **/
@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
public class FileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileServiceApplication.class, args);
	}

}

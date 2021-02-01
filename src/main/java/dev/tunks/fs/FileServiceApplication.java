package dev.tunks.fs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
public class FileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileServiceApplication.class, args);
	}

}

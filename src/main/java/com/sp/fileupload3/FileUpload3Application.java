package com.sp.fileupload3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(com.sp.fileupload3.controller.FileProperties.class)
public class FileUpload3Application {

	public static void main(String[] args) {
		SpringApplication.run(FileUpload3Application.class, args);
	}

}

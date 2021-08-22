package com.sp.fileupload3;

import com.sp.fileupload3.controller.FileService;
import com.sp.fileupload3.controller.UploadResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileUploadTest extends WebIntegrationTest {

    @Autowired
    private FileService fileService;
    private Path root;
    @BeforeEach
    void before() throws IOException {
        ClassPathResource testPath = new ClassPathResource("files");
        Path rootDir = testPath.getFile().toPath();
        root = rootDir.resolve("" + Math.abs(new Random().nextLong()));
        fileService.setRoot(root);
        fileService.init();
    }

    @AfterEach
    void after() throws IOException {
        if(Files.exists(root)) {
            FileSystemUtils.deleteRecursively(root);
        }
    }


    @DisplayName("1. 파일을 한개 업로드 한다.")
    @Test
    void test_1(){
        HttpEntity req = makeRequest(new ClassPathResource("test1.txt"));
        ResponseEntity<UploadResult> response = client.postForEntity(uri("/upload"), req, UploadResult.class);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("/files/test1.txt", response.getBody().getPath().get(0));

    }

    private HttpEntity makeRequest(Resource... resources) {
        MultiValueMap form = new LinkedMultiValueMap();
        if(resources.length == 1) {
            form.add("file", resources[0]);
        }else{
            Arrays.stream(resources).forEach(res->form.add("files", res));
        }
//        HttpHeaders header = new HttpHeaders();
//        header.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        HttpEntity req = new HttpEntity(form, null);
        return req;
    }


    @DisplayName("2. 파일을 2개 업로드 한다.")
    @Test
    void test_2(){
        HttpEntity req = makeRequest(
                new ClassPathResource("test1.txt"),
                new ClassPathResource("test2.txt")
        );
        ResponseEntity<UploadResult> response = client.postForEntity(uri("/uploads"),
                req, UploadResult.class);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("/files/test1.txt", response.getBody().getPath().get(0));
        assertEquals("/files/test2.txt", response.getBody().getPath().get(1));
    }



}

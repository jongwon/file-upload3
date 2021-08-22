package com.sp.fileupload3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.fileupload3.controller.FileService;
import com.sp.fileupload3.controller.UploadResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class AuthFileUploadTest {

    @TestConfiguration
    static class Config {

        @Bean
        public FileService fileService() throws IOException {
            FileService fileService = new FileService();
            return fileService;
        }
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FileService fileService;

    private Path root;
    private ObjectMapper objectMapper = new ObjectMapper();

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

    @DisplayName("1. 유저 아이디로 파일을 저장한다.")
    @Test
    @WithMockUser(username = "user1", roles={"USER"})
    void test_1() throws Exception {
        String respStr = mockMvc.perform(
                MockMvcRequestBuilders.multipart("/auth/upload")
                        .file(multipart("file", new ClassPathResource("test1.txt")))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ;
        UploadResult result = objectMapper.readValue(respStr, UploadResult.class);
        assertEquals("user1", result.getUsername());
        assertEquals("/files/test1.txt", result.getPath().get(0));
    }

    @DisplayName("2. 유저 아이디로 2개의 파일을 저장한다.")
    @Test
    @WithMockUser(username = "user1", roles={"USER"})
    void test_2() throws Exception {
        String respStr = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/auth/uploads")
                                .file(multipart("files", new ClassPathResource("test1.txt")))
                                .file(multipart("files", new ClassPathResource("test2.txt")))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ;
        UploadResult result = objectMapper.readValue(respStr, UploadResult.class);
        assertEquals("user1", result.getUsername());
        assertEquals("/files/test1.txt", result.getPath().get(0));
        assertEquals("/files/test2.txt", result.getPath().get(1));
    }

    private MockMultipartFile multipart(String name, Resource res) throws IOException {
        return new MockMultipartFile(
                name, res.getFilename(), MediaType.TEXT_PLAIN_VALUE, res.getInputStream()
        );
    }


}

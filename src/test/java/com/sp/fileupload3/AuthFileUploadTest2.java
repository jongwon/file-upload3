package com.sp.fileupload3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.fileupload3.controller.FileService;
import com.sp.fileupload3.controller.UploadResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class AuthFileUploadTest2 {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    private Path root;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void before() throws IOException {

    }

    @AfterEach
    void after() throws IOException {

    }

    @DisplayName("1. 유저 아이디로 파일을 저장한다.")
    @Test
    @WithMockUser(username = "user1", roles={"USER"})
    void test_1() throws Exception {
        doNothing().when(fileService).save(any(MultipartFile.class));
        String respStr = mockMvc.perform(
                MockMvcRequestBuilders.multipart("/auth/upload")
                        .file(multipart("file", new ClassPathResource("test1.txt")))
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
        doNothing().when(fileService).save(any(MultipartFile.class));
        String respStr = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/auth/uploads")
                                .file(multipart("files", new ClassPathResource("test1.txt")))
                                .file(multipart("files", new ClassPathResource("test2.txt")))
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

package com.sp.fileupload3;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.fileupload3.controller.FileError;
import com.sp.fileupload3.controller.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class 파일다운로드_테스트 {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    private ObjectMapper objectMapper = new ObjectMapper();


    @DisplayName("1. 파일을 다운로드 한다.")
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void test_1() throws Exception {

        Mockito.when(fileService.getFile(any())).thenReturn(new ClassPathResource("test1.txt").getFile().toPath());

        String fileContent = mockMvc.perform(MockMvcRequestBuilders.get("/files/download?path=test1.txt"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(fileContent).isEqualTo("hello jongwon");

    }

    @DisplayName("2. 이미지 파일을 다운로드 받는다. ")
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void test_2() throws Exception {
        File imageFile = new ClassPathResource("dog1.jpeg").getFile();
        Mockito.when(fileService.getFile(any())).thenReturn(imageFile.toPath());

        byte[] bytes = mockMvc.perform(MockMvcRequestBuilders.get("/files/download?path=dog1.jpeg"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        assertThat(bytes.length).isEqualTo(imageFile.length());
    }

    @DisplayName("3. 권한이 없는 사람은 파일을 받지 못한다.")
    @Test
    @WithMockUser(username = "user1", roles = {"CLIENT"})
    void test_3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/files/download?path=dog1.jpeg"))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("4. 파일이 없는 경우에는 오류를 내려준다.")
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void test_4() throws Exception {
        Mockito.when(fileService.getFile(any())).thenReturn(Path.of("no-exist.txt"));

        String errorMessage = mockMvc.perform(MockMvcRequestBuilders.get("/files/download?path=no-exist.txt"))
                .andExpect(status().is5xxServerError())
                .andReturn().getResponse().getContentAsString();

        FileError error = objectMapper.readValue(errorMessage, FileError.class);
        assertThat(error.getMessage()).isEqualTo("File not found");
    }


    @DisplayName("5. 디렉토리는 다운로드 할 수 없다.")
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void test_5() throws Exception {
        Mockito.when(fileService.getFile(any())).thenReturn(new ClassPathResource("files").getFile().toPath());

        String errorMessage = mockMvc.perform(MockMvcRequestBuilders.get("/files/download?path=files"))
                .andExpect(status().is5xxServerError())
                .andReturn().getResponse().getContentAsString();

        FileError error = objectMapper.readValue(errorMessage, FileError.class);
        assertThat(error.getMessage()).isEqualTo("You cannot download directory");
    }

}

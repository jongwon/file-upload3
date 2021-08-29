package com.sp.fileupload3;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.fileupload3.controller.FileError;
import com.sp.fileupload3.controller.FileException;
import com.sp.fileupload3.controller.FileService;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest()
public class 파일다운로드_테스트 {

    @TestConfiguration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    static class SecurityConfig extends WebSecurityConfigurerAdapter {

    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("1. 파일을 다운로드한다. - 파일이 없는 경우")
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void test_1() throws Exception {
        Mockito.when(fileService.getFile(any())).thenReturn(Path.of("not-existfile.txt"));

        String responseStr = mockMvc.perform(
                MockMvcRequestBuilders.get("/files/download?path=test1.txt")
//                        .with(csrf())
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        FileError error = objectMapper.readValue(responseStr, FileError.class);
        assertThat(error.getMessage()).isEqualTo("file not exist");
    }


    @DisplayName("2. 파일을 다운로드 한다.")
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void test_2() throws Exception {
        Mockito.when(fileService.getFile(any())).thenReturn(new ClassPathResource("test1.txt").getFile().toPath());
        String contentAsString = mockMvc.perform(
                MockMvcRequestBuilders.get("/files/download?path=test1.txt"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo("hello jongwon");
    }


    @DisplayName("3. 권한이 없으면 파일을 내려주지 않는다.")
    @Test
    @WithMockUser(username = "user1", roles = {"TESTER"})
    void test_3() throws Exception {
        Mockito.when(fileService.getFile(any())).thenReturn(new ClassPathResource("test1.txt").getFile().toPath());
        mockMvc.perform(MockMvcRequestBuilders.get("/files/download?path=test1.txt"))
                .andExpect(status().is4xxClientError());
    }


}

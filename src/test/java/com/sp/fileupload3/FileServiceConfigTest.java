package com.sp.fileupload3;

import com.sp.fileupload3.controller.FileProperties;
import com.sp.fileupload3.controller.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
        "sp.fileupload.dir=/Users/jongwon/dev/temp/test"
})
public class FileServiceConfigTest {

    @Autowired
    FileProperties fileProperties;

    @DisplayName("1. sp.fileupload.dir 이 잘 설정되는지 확인한다. ")
    @Test
    void test_1(){
        assertEquals("/Users/jongwon/dev/temp/test",
                fileProperties.getDir());

    }
}

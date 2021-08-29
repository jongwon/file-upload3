package com.sp.fileupload3.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class FileService {

    @Getter
    @Setter
    private Path root;

    @Value("${sp.fileupload.dir:upload-dir}")
    private String uploadDir;

    public void setUploadDir(String uploadDir){
        log.info("=======");
        this.uploadDir = uploadDir;
        log.info(uploadDir);
        this.root = Path.of(uploadDir);
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() throws IOException {
        if(!Files.exists(root)) {
            Files.createDirectories(root);
        }
    }


    public void save(MultipartFile file) throws IOException {
        file.transferTo(root.resolve(file.getOriginalFilename()));
    }




}

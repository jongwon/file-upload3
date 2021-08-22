package com.sp.fileupload3.controller;

import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileService {

    @Setter
    private Path root;

    public FileService() {
        // property... 에서 설정해서 가져와야 함.
        this.root = Path.of("upload-dir");
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

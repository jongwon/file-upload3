package com.sp.fileupload3.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.EntityResolver;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class FileController {

    private final FileService fileService;

    @PostMapping(value="/upload")
    public UploadResult upload(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(value="file") MultipartFile file
    ) throws IOException {
        fileService.save(file);
        return UploadResult.builder()
                .code(100)
                .userId(user.getUsername())
                .path(List.of("/files/"+file.getOriginalFilename()))
                .build();
    }

    @PostMapping(value="/uploads")
    public UploadResult uploads(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(value="files") MultipartFile[] file
    ){

        return UploadResult.builder()
                .code(100)
                .userId(user.getUsername())
                .path(Arrays.stream(file).map(f->{
                    try {
                        fileService.save(f);
                        return "/files/"+f.getOriginalFilename();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "error to save : "+f.getOriginalFilename();
                    }
                }).collect(Collectors.toList()))
                .build();
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/files/download")
    public void download(@RequestParam String path, HttpServletResponse response){
        Path file = fileService.getFile(path);
        if(!Files.exists(file)) throw new FileException("File not found");
        if(Files.isDirectory(file)) throw new FileException("You cannot download directory");

        try(OutputStream out = response.getOutputStream()){
            out.write(Files.readAllBytes(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ExceptionHandler({FileException.class})
    public ResponseEntity<FileError> fileException(FileException ex){
        return ResponseEntity
                .status(500)
                .body(new FileError(ex.getMessage()));
    }


}

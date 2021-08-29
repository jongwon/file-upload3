package com.sp.fileupload3.controller;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
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
    public void download(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(value="path") String path,
            HttpServletResponse response
    ){
        Path filePath = fileService.getFile(path);
        if(!Files.exists(filePath)) throw new FileException("file not exist");

        try(OutputStream out = response.getOutputStream()){
            out.write(Files.readAllBytes(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ExceptionHandler({FileException.class})
    public FileError exception(FileException exception){
        return new FileError(exception.getMessage());
    }


}

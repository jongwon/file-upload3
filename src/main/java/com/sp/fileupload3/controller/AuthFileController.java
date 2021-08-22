package com.sp.fileupload3.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(value="/auth")
@PreAuthorize("isAuthenticated()")
public class AuthFileController {

    private final FileService fileService;

    @PostMapping(value="/upload")
    public UploadResult upload(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value="file") MultipartFile file
    ) throws IOException {
        fileService.save(file);
        return UploadResult.builder()
                .code(100)
                .username(userDetails.getUsername())
                .path(List.of("/files/"+file.getOriginalFilename()))
                .build();
    }

    @PostMapping(value="/uploads")
    public UploadResult uploads(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value="files") MultipartFile[] file
    ){
        return UploadResult.builder()
                .code(100)
                .username(userDetails.getUsername())
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
}
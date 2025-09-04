package com.codecomet.linkedInProject.uploader_service.controller;

import com.cloudinary.Uploader;
import com.codecomet.linkedInProject.uploader_service.service.UploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class UploaderController {

    private final UploaderService uploaderService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file){
        String url = uploaderService.upload(file);
        return ResponseEntity.ok(url);
    }
}

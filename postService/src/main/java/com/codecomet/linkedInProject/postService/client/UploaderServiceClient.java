package com.codecomet.linkedInProject.postService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "uploader-service", path = "/uploads/file")
@Component
public interface UploaderServiceClient {

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file);



}

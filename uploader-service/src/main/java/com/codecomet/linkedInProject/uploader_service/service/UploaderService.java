package com.codecomet.linkedInProject.uploader_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface UploaderService {

    String upload(MultipartFile file);
}

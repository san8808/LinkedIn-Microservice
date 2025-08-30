package com.codecomet.linkedInProject.notification_service.controller;

import com.codecomet.linkedInProject.notification_service.dto.NotificationDto;
import com.codecomet.linkedInProject.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/core")
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllNotification(){

        List<NotificationDto> notificationDtoList = notificationService.getAllNotification();
        return ResponseEntity.ok(notificationDtoList);

    }


}

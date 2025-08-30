package com.codecomet.linkedInProject.notification_service.service;

import com.codecomet.linkedInProject.notification_service.auth.AuthContextHolder;
import com.codecomet.linkedInProject.notification_service.dto.NotificationDto;
import com.codecomet.linkedInProject.notification_service.entity.Notification;
import com.codecomet.linkedInProject.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public void addNotification(Notification notification){
        log.info("Adding notification to db, message: {}",notification.getMessage());
        notification= notificationRepository.save(notification);

        //send mailer to send email
        // FCM
    }

    public List<NotificationDto> getAllNotification() {

        Long userId = AuthContextHolder.getCurrentUserId();
        List<Notification> getAllNotification = notificationRepository.findAllByUserId(userId);

        return getAllNotification.stream().map( n-> modelMapper.map(n, NotificationDto.class)).collect(Collectors.toList());
    }
}

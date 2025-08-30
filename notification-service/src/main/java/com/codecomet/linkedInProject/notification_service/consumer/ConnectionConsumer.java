package com.codecomet.linkedInProject.notification_service.consumer;

import com.codecomet.linkedInProject.connectionService.event.ConnectionAccepted;
import com.codecomet.linkedInProject.connectionService.event.ConnectionRequested;
import com.codecomet.linkedInProject.notification_service.entity.Notification;
import com.codecomet.linkedInProject.notification_service.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ConnectionConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "connection_requested_topic")
    public void handleConnectionRequested(ConnectionRequested connectionRequested){

        log.info("handleConnectionRequestd: {}", connectionRequested);

        String message = String.format("You got a new connection request from %s with userId: %d",connectionRequested.getSenderName(),connectionRequested.getSenderId());

        Notification notification = Notification.builder()
                .userId(connectionRequested.getReceiverId())
                .message(message)
                .build();
        notificationService.addNotification(notification);
    }

    @KafkaListener(topics = "connection_accepted_topic")
    public void handleConnectionAccepted(ConnectionAccepted connectionAccepted){

        log.info("handleConnectionAccepted: {}", connectionAccepted);

        String message = String.format("%s accepted you connection request, having id: %d",connectionAccepted.getReceiverName(),connectionAccepted.getReceiverId());

        Notification notification = Notification.builder()
                .userId(connectionAccepted.getSenderId())
                .message(message)
                .build();
        notificationService.addNotification(notification);
    }




}

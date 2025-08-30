package com.codecomet.linkedInProject.notification_service.repository;

import com.codecomet.linkedInProject.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}

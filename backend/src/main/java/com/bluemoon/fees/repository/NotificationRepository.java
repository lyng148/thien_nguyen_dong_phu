package com.bluemoon.fees.repository;

import com.bluemoon.fees.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Notification> findByReadFalseOrderByCreatedAtDesc();
    
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :notificationId")
    void markAsRead(Long notificationId);
    
    long countByReadFalse();
} 
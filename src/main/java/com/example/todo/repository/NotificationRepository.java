package com.example.todo.repository;

import com.example.todo.entity.Notification;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** Most recent notifications, newest first. */
    List<Notification> findByOrderByIdDesc(Limit limit);

    /** Count of unread notifications (for the UI bell badge). */
    long countByReadFalse();

    /** Mark all unread notifications as read; returns the number updated. */
    @Modifying
    @Transactional
    @Query("update Notification n set n.read = true where n.read = false")
    int markAllRead();
}

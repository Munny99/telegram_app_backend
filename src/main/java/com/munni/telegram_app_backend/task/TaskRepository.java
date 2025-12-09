package com.munni.telegram_app_backend.task;

import com.munni.telegram_app_backend.enums.TaskStatus;
import com.munni.telegram_app_backend.personnel.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task , Long> {
//
//    List<Task> findByUserAndStatus(User user, TaskStatus status);
//    List<Task> findByUserOrderByCreatedAtDesc(User user);
//    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);



}

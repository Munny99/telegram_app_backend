package com.munni.telegram_app_backend.module.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task , Long> {
//
//    List<Task> findByUserAndStatus(User user, TaskStatus status);
//    List<Task> findByUserOrderByCreatedAtDesc(User user);
//    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);



}

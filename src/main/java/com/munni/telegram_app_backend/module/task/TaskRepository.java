package com.munni.telegram_app_backend.module.task;

import com.munni.telegram_app_backend.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task , Long> {
//
//    List<Task> findByUserAndStatus(User user, TaskStatus status);
//    List<Task> findByUserOrderByCreatedAtDesc(User user);
//    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);

    /**
     * Find all tasks for a specific user
     */
    List<Task> findByUserId(Long userId);

    /**
     * Find tasks by user ID and status
     */
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);

    /**
     * Count total tasks for a user
     */
    long countByUserId(Long userId);

    /**
     * Count tasks by user ID and status
     */
    long countByUserIdAndStatus(Long userId, TaskStatus status);

    /**
     * Find tasks by user ID ordered by creation date
     */
    List<Task> findByUserIdOrderByIdDesc(Long userId);

    /**
     * Find pending tasks for a user ordered by reward amount (highest first)
     */
    List<Task> findByUserIdAndStatusOrderByRewardAmountDesc(Long userId, TaskStatus status);


}

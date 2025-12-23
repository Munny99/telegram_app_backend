package com.munni.telegram_app_backend.module.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTaskProgressRepository extends JpaRepository<UserTaskProgress, Long> {

    /**
     * Find progress for a specific user and task
     */
    Optional<UserTaskProgress> findByUserIdAndGlobalTaskId(Long userId, Long globalTaskId);

    /**
     * Find all completed tasks for a user
     */
    List<UserTaskProgress> findByUserIdAndCompletedTrue(Long userId);

    /**
     * Count completed tasks for a user
     */
    long countByUserIdAndCompletedTrue(Long userId);

    /**
     * Check if user completed a specific task
     */
    boolean existsByUserIdAndGlobalTaskIdAndCompletedTrue(Long userId, Long globalTaskId);
}
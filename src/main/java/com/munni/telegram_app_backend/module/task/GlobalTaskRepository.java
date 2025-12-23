package com.munni.telegram_app_backend.module.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalTaskRepository extends JpaRepository<GlobalTask, Long> {

    /**
     * Find all active global tasks
     */
    List<GlobalTask> findByIsActiveTrueOrderByIdAsc();

    /**
     * Find all global tasks (including inactive)
     */
    List<GlobalTask> findAllByOrderByIdAsc();

    /**
     * Count active tasks
     */
    long countByIsActiveTrue();
}
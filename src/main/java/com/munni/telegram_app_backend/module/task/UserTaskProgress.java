package com.munni.telegram_app_backend.module.task;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Tracks which users have completed which global tasks
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_task_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "global_task_id"}))
public class UserTaskProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long globalTaskId;

    @Column(nullable = false)
    private Boolean completed = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime completedAt;

    @Column
    private Integer verificationAttempts = 0;
}
package com.munni.telegram_app_backend.module.task;

import com.munni.telegram_app_backend.enums.TaskType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "global_tasks")
public class GlobalTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Task type is required")
    private TaskType taskType;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Task title must not exceed 255 characters")
    private String taskTitle;

    @Column(length = 1000)
    @Size(max = 1000, message = "Task description must not exceed 1000 characters")
    private String taskDescription;

    @Column(length = 1000)
    @Size(max = 1000, message = "Task link must not exceed 1000 characters")
    private String taskLink;

    @Column(nullable = false, precision = 20, scale = 2)
    @NotNull(message = "Reward amount is required")
    private BigDecimal rewardAmount;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
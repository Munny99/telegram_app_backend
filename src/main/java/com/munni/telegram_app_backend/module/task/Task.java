package com.munni.telegram_app_backend.module.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.munni.telegram_app_backend.enums.TaskStatus;
import com.munni.telegram_app_backend.enums.TaskType;
import com.munni.telegram_app_backend.module.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
@ToString(onlyExplicitlyIncluded = true)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Task type is required")
    private TaskType taskType; // TELEGRAM_CHANNEL, YOUTUBE, CUSTOM_LINK


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


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Task status is required")
    private TaskStatus status = TaskStatus.PENDING; // Default PENDING


}

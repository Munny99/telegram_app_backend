package com.munni.telegram_app_backend.module.task;

import com.munni.telegram_app_backend.enums.TaskStatus;
import com.munni.telegram_app_backend.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResDto {

    private Long id;

    private Long userId;
    private TaskType taskType;
    private String taskTitle;
    private String taskDescription;
    private String taskLink;
    private BigDecimal rewardAmount;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

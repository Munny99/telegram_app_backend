package com.munni.telegram_app_backend.task;

import com.munni.telegram_app_backend.enums.TaskStatus;
import com.munni.telegram_app_backend.enums.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskReqDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Task type is required")
    private TaskType taskType;  // TELEGRAM_CHANNEL, YOUTUBE, CUSTOM_LINK

    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Task title must not exceed 255 characters")
    private String taskTitle;

    @Size(max = 1000, message = "Task description must not exceed 1000 characters")
    private String taskDescription;

    @Size(max = 1000, message = "Task link must not exceed 1000 characters")
    private String taskLink;

    @NotNull(message = "Reward amount is required")
    private BigDecimal rewardAmount;

    @NotNull(message = "Task status is required")
    private TaskStatus status;


}

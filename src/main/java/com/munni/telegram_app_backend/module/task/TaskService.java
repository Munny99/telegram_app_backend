package com.munni.telegram_app_backend.module.task;

import com.munni.telegram_app_backend.enums.TaskStatus;
import com.munni.telegram_app_backend.exception.CustomException;
import com.munni.telegram_app_backend.module.user.User;
import com.munni.telegram_app_backend.module.user.UserRepo;
import com.munni.telegram_app_backend.util.PaginationUtil;
import com.munni.telegram_app_backend.util.ResponseUtils;
import com.munni.telegram_app_backend.util.response.BaseApiResponseDTO;
import com.munni.telegram_app_backend.util.response.CustomPageResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    @Autowired private TaskMapper taskmapper;
    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepo userRepository;

    /**
     * Get all tasks for a specific user
     */
    public List<Task> getTasksForUser(Long userId) {
        log.info("Fetching tasks for user: {}", userId);
        return taskRepository.findByUserId(userId);
    }

    /**
     * Get all pending tasks for a user
     */
    public List<Task> getPendingTasksForUser(Long userId) {
        return taskRepository.findByUserIdAndStatus(userId, TaskStatus.PENDING);
    }

    /**
     * Get all completed tasks for a user
     */
    public List<Task> getCompletedTasksForUser(Long userId) {
        return taskRepository.findByUserIdAndStatus(userId, TaskStatus.COMPLETED);
    }

    /**
     * Create a new task for a user
     */
    @Transactional
    public Task createTask(Long userId, Task task) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setUser(user);
        task.setStatus(TaskStatus.PENDING);

        Task savedTask = taskRepository.save(task);

        // Update user's total tasks count
        user.setTotalTasks(user.getTotalTasks() + 1);
        userRepository.save(user);

        log.info("Created new task {} for user {}", savedTask.getId(), userId);
        return savedTask;
    }

    /**
     * Verify and complete a task
     * In a real implementation, you would verify the task completion here
     * (e.g., check if user subscribed to Telegram channel, watched YouTube video, etc.)
     */
    @Transactional
    public Task verifyAndCompleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Verify the task belongs to the user
        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Task does not belong to this user");
        }

        // Check if task is already completed
        if (task.getStatus() == TaskStatus.COMPLETED) {
            log.info("Task {} already completed", taskId);
            return task;
        }

        // Mark task as completed
        task.setStatus(TaskStatus.COMPLETED);
        Task completedTask = taskRepository.save(task);

        // Update user statistics and balance
        User user = task.getUser();

        // Add reward to user's balances
        BigDecimal reward = task.getRewardAmount();
        user.setTotalEarnings(user.getTotalEarnings().add(reward));

        // Convert reward to cents/smallest unit for pending balance
        // Assuming pendingBalance is stored as cents (multiply by 100)
        Long rewardInCents = reward.multiply(new BigDecimal(100)).longValue();
        user.setPendingBalance(user.getPendingBalance() + rewardInCents);

        // Increment completed tasks
        user.setCompletedTasks(user.getCompletedTasks() + 1);

        userRepository.save(user);

        log.info("Task {} completed by user {}. Reward: ${}", taskId, userId, reward);
        return completedTask;
    }

    /**
     * Get a specific task by ID
     */
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
    }

    /**
     * Update task details
     */
    @Transactional
    public Task updateTask(Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (taskDetails.getTaskTitle() != null) {
            task.setTaskTitle(taskDetails.getTaskTitle());
        }
        if (taskDetails.getTaskDescription() != null) {
            task.setTaskDescription(taskDetails.getTaskDescription());
        }
        if (taskDetails.getTaskLink() != null) {
            task.setTaskLink(taskDetails.getTaskLink());
        }
        if (taskDetails.getRewardAmount() != null) {
            task.setRewardAmount(taskDetails.getRewardAmount());
        }
        if (taskDetails.getTaskType() != null) {
            task.setTaskType(taskDetails.getTaskType());
        }

        return taskRepository.save(task);
    }

    /**
     * Delete a task
     */
    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // If task was completed, we might want to adjust user's stats
        if (task.getStatus() == TaskStatus.COMPLETED) {
            User user = task.getUser();
            user.setCompletedTasks(user.getCompletedTasks() - 1);
            user.setTotalTasks(user.getTotalTasks() - 1);
            userRepository.save(user);
        } else {
            User user = task.getUser();
            user.setTotalTasks(user.getTotalTasks() - 1);
            userRepository.save(user);
        }

        taskRepository.delete(task);
        log.info("Deleted task {}", taskId);
    }

    /**
     * Get total number of tasks for a user
     */
    public long getTotalTasksCount(Long userId) {
        return taskRepository.countByUserId(userId);
    }

    /**
     * Get total number of completed tasks for a user
     */
    public long getCompletedTasksCount(Long userId) {
        return taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
    }

    /**
     * Calculate total potential earnings for pending tasks
     */
    public BigDecimal calculatePendingEarnings(Long userId) {
        List<Task> pendingTasks = getPendingTasksForUser(userId);
        return pendingTasks.stream()
                .map(Task::getRewardAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

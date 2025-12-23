package com.munni.telegram_app_backend.module.task;

import com.munni.telegram_app_backend.enums.TaskStatus;
import com.munni.telegram_app_backend.enums.TaskType;
import com.munni.telegram_app_backend.module.user.User;
import com.munni.telegram_app_backend.module.user.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing global tasks with real verification
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalTaskService {

    private final GlobalTaskRepository globalTaskRepository;
    private final UserTaskProgressRepository userTaskProgressRepository;
    private final UserRepo userRepository;
    private final TelegramBotService telegramBotService;

    private static final int MAX_VERIFICATION_ATTEMPTS = 3;
    private static final int MIN_WAIT_SECONDS = 5;

    /**
     * Get all active global tasks with user's completion status
     */
    public List<TaskDTO> getTasksForUser(Long userId) {
        List<GlobalTask> allTasks = globalTaskRepository.findByIsActiveTrueOrderByIdAsc();

        return allTasks.stream().map(globalTask -> {
            Optional<UserTaskProgress> progress = userTaskProgressRepository
                    .findByUserIdAndGlobalTaskId(userId, globalTask.getId());

            TaskStatus status;
            if (progress.isPresent() && progress.get().getCompleted()) {
                status = TaskStatus.COMPLETED;
            } else {
                status = TaskStatus.PENDING;
            }

            return new TaskDTO(
                    globalTask.getId(),
                    globalTask.getTaskType(),
                    globalTask.getTaskTitle(),
                    globalTask.getTaskDescription(),
                    globalTask.getTaskLink(),
                    globalTask.getRewardAmount(),
                    status
            );
        }).collect(Collectors.toList());
    }

    /**
     * Start a task - marks it as initiated by user
     */
    @Transactional
    public TaskDTO startTask(Long taskId, Long userId) {
        GlobalTask globalTask = globalTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!globalTask.getIsActive()) {
            throw new RuntimeException("Task is no longer active");
        }

        // Check if already completed
        Optional<UserTaskProgress> existingProgress = userTaskProgressRepository
                .findByUserIdAndGlobalTaskId(userId, taskId);

        if (existingProgress.isPresent() && existingProgress.get().getCompleted()) {
            return createTaskDTO(globalTask, TaskStatus.COMPLETED);
        }

        // Create or reset progress record
        if (existingProgress.isPresent()) {
            UserTaskProgress progress = existingProgress.get();
            // Reset attempts if user is starting again
            progress.setVerificationAttempts(0);
            progress.setStartedAt(LocalDateTime.now());
            userTaskProgressRepository.save(progress);
            log.info("User {} restarted task {}", userId, taskId);
        } else {
            UserTaskProgress progress = UserTaskProgress.builder()
                    .userId(userId)
                    .globalTaskId(taskId)
                    .completed(false)
                    .verificationAttempts(0)
                    .startedAt(LocalDateTime.now())
                    .build();
            userTaskProgressRepository.save(progress);
            log.info("User {} started task {}", userId, taskId);
        }

        return createTaskDTO(globalTask, TaskStatus.PENDING);
    }

    /**
     * Verify and complete a task with REAL verification
     */
    @Transactional
    public TaskDTO verifyAndCompleteTask(Long taskId, Long userId, String telegramUserId) {
        GlobalTask globalTask = globalTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!globalTask.getIsActive()) {
            throw new RuntimeException("Task is no longer active");
        }

        // Get progress record
        UserTaskProgress progress = userTaskProgressRepository
                .findByUserIdAndGlobalTaskId(userId, taskId)
                .orElseThrow(() -> new RuntimeException("Please start the task first by clicking 'Start Task'"));

        // Check if already completed
        if (progress.getCompleted()) {
            log.info("Task {} already completed by user {}", taskId, userId);
            return createTaskDTO(globalTask, TaskStatus.COMPLETED);
        }

        // Check verification attempts limit
        if (progress.getVerificationAttempts() >= MAX_VERIFICATION_ATTEMPTS) {
            throw new RuntimeException("Maximum verification attempts reached. Please restart the task.");
        }

        // Increment verification attempts
        progress.setVerificationAttempts(progress.getVerificationAttempts() + 1);
        userTaskProgressRepository.save(progress);

        // Check minimum time (at least 5 seconds to prevent instant completion)
        LocalDateTime startedAt = progress.getStartedAt();
        LocalDateTime now = LocalDateTime.now();

        if (startedAt.plusSeconds(MIN_WAIT_SECONDS).isAfter(now)) {
            long secondsElapsed = java.time.Duration.between(startedAt, now).getSeconds();
            throw new RuntimeException(String.format(
                    "Please wait at least %d seconds before verifying (%d seconds elapsed)",
                    MIN_WAIT_SECONDS, secondsElapsed
            ));
        }

        // Perform REAL verification
        boolean isVerified = verifyTaskCompletion(globalTask, telegramUserId);

        if (!isVerified) {
            log.warn("Task {} verification FAILED for user {} (attempt {}/{})",
                    taskId, userId, progress.getVerificationAttempts(), MAX_VERIFICATION_ATTEMPTS);
            throw new RuntimeException(String.format(
                    "Verification failed. Please make sure you completed the task. Attempts remaining: %d",
                    MAX_VERIFICATION_ATTEMPTS - progress.getVerificationAttempts()
            ));
        }

        // Mark as completed
        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        userTaskProgressRepository.save(progress);

        // Award the reward
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setTotalEarnings(user.getTotalEarnings().add(globalTask.getRewardAmount()));
        Long rewardInCents = globalTask.getRewardAmount().multiply(new BigDecimal(100)).longValue();
        user.setPendingBalance(user.getPendingBalance() + rewardInCents);
        user.setCompletedTasks(user.getCompletedTasks() + 1);
        userRepository.save(user);

        log.info("Task {} VERIFIED and completed by user {}. Reward: ${}",
                taskId, userId, globalTask.getRewardAmount());

        return createTaskDTO(globalTask, TaskStatus.COMPLETED);
    }

    /**
     * Verify task completion with REAL checks - NO shortcuts!
     */
    private boolean verifyTaskCompletion(GlobalTask task, String telegramUserId) {

        switch (task.getTaskType()) {
            case TELEGRAM_CHANNEL:
                // STRICT: Must verify channel membership via Bot API
                return verifyTelegramChannel(task, telegramUserId);

            case YOUTUBE:
                // YouTube verification requires YouTube API or manual approval
                // For now, return false unless you implement YouTube API
                log.warn("YOUTUBE task verification not implemented yet - returning false");
                throw new RuntimeException("YouTube task verification is not available yet. Please contact support.");

            case CUSTOM_LINK:
                // Custom links require tracking or manual verification
                log.warn("CUSTOM_LINK task verification not implemented yet - returning false");
                throw new RuntimeException("Custom link verification is not available yet. Please contact support.");

            default:
                return false;
        }
    }

    /**
     * Verify if user joined Telegram channel using Bot API
     * This is the ONLY way to verify - no shortcuts!
     */
    private boolean verifyTelegramChannel(GlobalTask task, String telegramUserId) {
        String channelUsername = telegramBotService.extractChannelUsername(task.getTaskLink());

        if (channelUsername == null) {
            log.error("Could not extract channel username from link: {}", task.getTaskLink());
            throw new RuntimeException("Invalid channel link configuration. Please contact support.");
        }

        // CRITICAL: Check if user is ACTUALLY a member of the channel
        boolean isMember = telegramBotService.isUserMemberOfChannel(telegramUserId, channelUsername);

        if (!isMember) {
            log.info("VERIFICATION FAILED: User {} is NOT a member of channel {}",
                    telegramUserId, channelUsername);
            return false;
        }

        log.info("VERIFICATION SUCCESS: User {} confirmed as member of channel {}",
                telegramUserId, channelUsername);
        return true;
    }

    /**
     * Get total number of active tasks
     */
    public long getTotalActiveTasks() {
        return globalTaskRepository.countByIsActiveTrue();
    }

    /**
     * Get user's task statistics
     */
    public TaskStatistics getUserTaskStatistics(Long userId) {
        long totalTasks = globalTaskRepository.countByIsActiveTrue();
        long completedTasks = userTaskProgressRepository.countByUserIdAndCompletedTrue(userId);

        return TaskStatistics.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(totalTasks - completedTasks)
                .completionRate(totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0.0)
                .build();
    }

    // ==================== ADMIN METHODS ====================

    @Transactional
    public GlobalTask createGlobalTask(GlobalTask task) {
        task.setIsActive(true);
        GlobalTask saved = globalTaskRepository.save(task);

        // Update all users' total task count
        List<User> allUsers = userRepository.findAll();
        allUsers.forEach(user -> {
            user.setTotalTasks(user.getTotalTasks() + 1);
        });
        userRepository.saveAll(allUsers);

        log.info("Admin created new global task: {}", saved.getTaskTitle());
        return saved;
    }

    @Transactional
    public GlobalTask updateGlobalTask(Long taskId, GlobalTask taskDetails) {
        GlobalTask task = globalTaskRepository.findById(taskId)
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

        return globalTaskRepository.save(task);
    }

    @Transactional
    public void deactivateGlobalTask(Long taskId) {
        GlobalTask task = globalTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setIsActive(false);
        globalTaskRepository.save(task);

        log.info("Admin deactivated global task: {}", task.getTaskTitle());
    }

    public List<GlobalTask> getAllGlobalTasks() {
        return globalTaskRepository.findAllByOrderByIdAsc();
    }

    private TaskDTO createTaskDTO(GlobalTask globalTask, TaskStatus status) {
        return new TaskDTO(
                globalTask.getId(),
                globalTask.getTaskType(),
                globalTask.getTaskTitle(),
                globalTask.getTaskDescription(),
                globalTask.getTaskLink(),
                globalTask.getRewardAmount(),
                status
        );
    }

    // DTOs
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class TaskDTO {
        private Long id;
        private TaskType taskType;
        private String taskTitle;
        private String taskDescription;
        private String taskLink;
        private BigDecimal rewardAmount;
        private TaskStatus status;
    }

    @lombok.Data
    @lombok.Builder
    public static class TaskStatistics {
        private long totalTasks;
        private long completedTasks;
        private long pendingTasks;
        private double completionRate;
    }
}
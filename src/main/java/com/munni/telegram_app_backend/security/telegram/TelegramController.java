package com.munni.telegram_app_backend.security.telegram;

import com.munni.telegram_app_backend.module.task.GlobalTask;
import com.munni.telegram_app_backend.module.task.GlobalTaskService;
import com.munni.telegram_app_backend.module.task.TelegramBotService;
import com.munni.telegram_app_backend.module.user.User;
import com.munni.telegram_app_backend.module.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TelegramController {

    private final TelegramAuthService telegramAuthService;
    private final UserService userService;
    private final GlobalTaskService globalTaskService;
    private final TelegramBotService telegramBotService;

    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody Map<String, String> request) {
        try {
            String initData = request.get("initData");

            if (initData == null || initData.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("initData is required"));
            }

            log.info("Authenticating Telegram user...");

            TelegramAuthService.TelegramUserData telegramData =
                    telegramAuthService.validateAndExtractUser(initData);

            User user = userService.findOrCreateUser(telegramData);
            user = userService.updateUserFromTelegram(user, telegramData);

            log.info("User authenticated successfully: {}", user.getTelegramId());

            return ResponseEntity.ok(createUserResponse(user, telegramData));

        } catch (SecurityException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid authentication data"));
        } catch (Exception e) {
            log.error("Authentication error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Authentication failed: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("X-Telegram-Init-Data") String initData) {
        try {
            if (initData == null || initData.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("X-Telegram-Init-Data header is required"));
            }

            TelegramAuthService.TelegramUserData telegramData =
                    telegramAuthService.validateAndExtractUser(initData);

            User user = userService.findByTelegramId(telegramData.getTelegramId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(createUserResponse(user, telegramData));

        } catch (SecurityException e) {
            log.error("Authorization failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid authentication data"));
        } catch (Exception e) {
            log.error("Get user error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to get user: " + e.getMessage()));
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getUserTasks(@RequestHeader("X-Telegram-Init-Data") String initData) {
        try {
            if (initData == null || initData.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("X-Telegram-Init-Data header is required"));
            }

            TelegramAuthService.TelegramUserData telegramData =
                    telegramAuthService.validateAndExtractUser(initData);

            User user = userService.findByTelegramId(telegramData.getTelegramId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<GlobalTaskService.TaskDTO> tasks = globalTaskService.getTasksForUser(user.getId());

            log.info("Retrieved {} GLOBAL tasks for user {}", tasks.size(), user.getTelegramId());

            return ResponseEntity.ok(tasks);

        } catch (SecurityException e) {
            log.error("Authorization failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid authentication data"));
        } catch (Exception e) {
            log.error("Get tasks error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to get tasks: " + e.getMessage()));
        }
    }

    /**
     * Start a task - called when user clicks on a task
     */
    @PostMapping("/tasks/{taskId}/start")
    public ResponseEntity<?> startTask(
            @PathVariable Long taskId,
            @RequestHeader("X-Telegram-Init-Data") String initData) {
        try {
            if (initData == null || initData.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("X-Telegram-Init-Data header is required"));
            }

            TelegramAuthService.TelegramUserData telegramData =
                    telegramAuthService.validateAndExtractUser(initData);

            User user = userService.findByTelegramId(telegramData.getTelegramId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            GlobalTaskService.TaskDTO startedTask = globalTaskService.startTask(taskId, user.getId());

            log.info("User {} started task {}", user.getTelegramId(), taskId);

            return ResponseEntity.ok(startedTask);

        } catch (SecurityException e) {
            log.error("Authorization failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid authentication data"));
        } catch (Exception e) {
            log.error("Start task error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to start task: " + e.getMessage()));
        }
    }

    /**
     * Verify and complete a task - checks if user actually completed the task
     */
    @PostMapping("/tasks/{taskId}/verify")
    public ResponseEntity<?> verifyTask(
            @PathVariable Long taskId,
            @RequestHeader("X-Telegram-Init-Data") String initData) {
        try {
            if (initData == null || initData.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("X-Telegram-Init-Data header is required"));
            }

            TelegramAuthService.TelegramUserData telegramData =
                    telegramAuthService.validateAndExtractUser(initData);

            User user = userService.findByTelegramId(telegramData.getTelegramId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Pass telegram user ID for channel membership verification
            GlobalTaskService.TaskDTO completedTask =
                    globalTaskService.verifyAndCompleteTask(taskId, user.getId(), telegramData.getTelegramId());

            log.info("GLOBAL task {} verified and completed by user {}", taskId, user.getTelegramId());

            return ResponseEntity.ok(completedTask);

        } catch (SecurityException e) {
            log.error("Authorization failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid authentication data"));
        } catch (Exception e) {
            log.error("Verify task error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // ==================== DEBUG ENDPOINTS ====================

    /**
     * Test bot configuration
     */
    @GetMapping("/debug/bot-status")
    public ResponseEntity<?> testBotConfiguration() {
        try {
            TelegramBotService.BotStatus status = telegramBotService.testBotConfiguration();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error testing bot configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Test bot access to a specific channel
     */
    @GetMapping("/debug/channel-access/{channelUsername}")
    public ResponseEntity<?> testChannelAccess(@PathVariable String channelUsername) {
        try {
            TelegramBotService.ChannelAccessStatus status =
                    telegramBotService.testChannelAccess(channelUsername);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error testing channel access", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Test if a specific user is a member of a channel
     */
    @GetMapping("/debug/check-membership/{channelUsername}/{userId}")
    public ResponseEntity<?> testMembership(
            @PathVariable String channelUsername,
            @PathVariable String userId) {
        try {
            boolean isMember = telegramBotService.isUserMemberOfChannel(userId, channelUsername);

            Map<String, Object> response = new HashMap<>();
            response.put("channelUsername", channelUsername);
            response.put("userId", userId);
            response.put("isMember", isMember);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error testing membership", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyInitData(@RequestBody Map<String, String> request) {
        try {
            String initData = request.get("initData");

            if (initData == null || initData.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("initData is required"));
            }

            TelegramAuthService.TelegramUserData telegramData =
                    telegramAuthService.validateAndExtractUser(initData);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("telegramId", telegramData.getTelegramId());
            response.put("firstName", telegramData.getFirstName());
            response.put("username", telegramData.getUsername());

            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Verification error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Verification failed: " + e.getMessage()));
        }
    }

    // ==================== ADMIN ENDPOINTS ====================

    @PostMapping("/admin/tasks")
    public ResponseEntity<?> createGlobalTask(
            @RequestBody GlobalTask task,
            @RequestHeader("X-Admin-Key") String adminKey) {
        try {
            if (!"YOUR_ADMIN_SECRET_KEY".equals(adminKey)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("Unauthorized"));
            }

            GlobalTask createdTask = globalTaskService.createGlobalTask(task);
            log.info("Admin created global task: {}", createdTask.getTaskTitle());

            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);

        } catch (Exception e) {
            log.error("Create global task error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to create task: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/tasks")
    public ResponseEntity<?> getAllGlobalTasks(@RequestHeader("X-Admin-Key") String adminKey) {
        try {
            if (!"YOUR_ADMIN_SECRET_KEY".equals(adminKey)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("Unauthorized"));
            }

            List<GlobalTask> tasks = globalTaskService.getAllGlobalTasks();
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            log.error("Get all global tasks error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to get tasks: " + e.getMessage()));
        }
    }

    @PutMapping("/admin/tasks/{taskId}")
    public ResponseEntity<?> updateGlobalTask(
            @PathVariable Long taskId,
            @RequestBody GlobalTask task,
            @RequestHeader("X-Admin-Key") String adminKey) {
        try {
            if (!"YOUR_ADMIN_SECRET_KEY".equals(adminKey)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("Unauthorized"));
            }

            GlobalTask updatedTask = globalTaskService.updateGlobalTask(taskId, task);
            return ResponseEntity.ok(updatedTask);

        } catch (Exception e) {
            log.error("Update global task error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to update task: " + e.getMessage()));
        }
    }

    @DeleteMapping("/admin/tasks/{taskId}")
    public ResponseEntity<?> deactivateGlobalTask(
            @PathVariable Long taskId,
            @RequestHeader("X-Admin-Key") String adminKey) {
        try {
            if (!"YOUR_ADMIN_SECRET_KEY".equals(adminKey)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("Unauthorized"));
            }

            globalTaskService.deactivateGlobalTask(taskId);
            return ResponseEntity.ok(Map.of("message", "Task deactivated successfully"));

        } catch (Exception e) {
            log.error("Deactivate global task error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to deactivate task: " + e.getMessage()));
        }
    }

    private Map<String, Object> createUserResponse(User user, TelegramAuthService.TelegramUserData telegramData) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("telegramId", user.getTelegramId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("userName", user.getUsername());
        response.put("referralCode", user.getReferralCode());
        response.put("totalEarnings", user.getTotalEarnings());
        response.put("totalWithdrawn", user.getTotalWithdrawn());
        response.put("pendingBalance", user.getPendingBalance());
        response.put("totalReferrals", user.getTotalReferrals());
        response.put("completedTasks", user.getCompletedTasks());
        response.put("totalTasks", globalTaskService.getTotalActiveTasks());
        response.put("rating", user.getRating());
        response.put("isActive", user.getIsActive());
        response.put("isPremium", telegramData.getIsPremium());
        response.put("languageCode", telegramData.getLanguageCode());
        response.put("photoUrl", telegramData.getPhotoUrl());
        return response;
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}
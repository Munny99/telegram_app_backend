package com.munni.telegram_app_backend.security.telegram;

import com.munni.telegram_app_backend.module.user.User;
import com.munni.telegram_app_backend.module.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Configure properly for production
public class TelegramController {

    private final TelegramAuthService telegramAuthService;
    private final UserService userService;

    /**
     * Authenticate user with Telegram initData and return user details
     */
    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody Map<String, String> request) {
        try {
            String initData = request.get("initData");
            
            if (initData == null || initData.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("initData is required"));
            }

            log.info("Authenticating Telegram user...");
            
            // Validate and extract Telegram user data
            TelegramAuthService.TelegramUserData telegramData = 
                    telegramAuthService.validateAndExtractUser(initData);

            // Find or create user in database
            User user = userService.findOrCreateUser(telegramData);

            // Update user data if needed
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

    /**
     * Get current user details using initData in header
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("X-Telegram-Init-Data") String initData) {
        try {
            if (initData == null || initData.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("X-Telegram-Init-Data header is required"));
            }

            // Validate and extract Telegram user data
            TelegramAuthService.TelegramUserData telegramData = 
                    telegramAuthService.validateAndExtractUser(initData);

            // Find user in database
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

    /**
     * Verify if initData is valid (useful for debugging)
     */
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

    private Map<String, Object> createUserResponse(User user, TelegramAuthService.TelegramUserData telegramData) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("telegramId", user.getTelegramId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("userName", user.getUsername());
        response.put("email", user.getEmail());
        response.put("referralCode", user.getReferralCode());
        response.put("totalEarnings", user.getTotalEarnings());
        response.put("totalWithdrawn", user.getTotalWithdrawn());
        response.put("pendingBalance", user.getPendingBalance());
        response.put("totalReferrals", user.getTotalReferrals());
        response.put("completedTasks", user.getCompletedTasks());
        response.put("totalTasks", user.getTotalTasks());
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
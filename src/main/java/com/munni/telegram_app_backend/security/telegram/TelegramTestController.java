package com.munni.telegram_app_backend.security.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Test controller to verify Telegram integration without frontend
 * Remove this in production!
 */
@Slf4j
@RestController
@RequestMapping("/api/telegram/test")
@RequiredArgsConstructor
public class TelegramTestController {

    private final TelegramAuthService telegramAuthService;

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Telegram service is running");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * Generate sample initData for testing
     * This simulates what Telegram would send
     */
    @GetMapping("/generate-sample")
    public Map<String, Object> generateSample() {
        Map<String, Object> response = new HashMap<>();
        
        // Sample user data (replace with your actual Telegram user data for testing)
        String sampleUser = "{\"id\":123456789,\"first_name\":\"John\",\"last_name\":\"Doe\",\"username\":\"johndoe\",\"language_code\":\"en\",\"is_premium\":false}";
        
        // Current timestamp
        long authDate = System.currentTimeMillis() / 1000;
        
        // Sample query_id
        String queryId = "AAHdF6IQAAAAAN0XohDhrOrc";
        
        // This is what the initData would look like (without hash - you need Telegram to generate the real hash)
        String sampleInitData = String.format(
            "query_id=%s&user=%s&auth_date=%d&hash=PLACEHOLDER_HASH",
            queryId,
            sampleUser.replace(" ", "%20"),
            authDate
        );
        
        response.put("note", "This is a SAMPLE. Real initData must come from Telegram with a valid hash.");
        response.put("sampleInitData", sampleInitData);
        response.put("userJson", sampleUser);
        response.put("authDate", authDate);
        response.put("instructions", "To get real initData: Open your bot in Telegram, launch the Mini App, and capture the window.Telegram.WebApp.initData value");
        
        return response;
    }

    /**
     * Test the auth service with sample data
     * This will help debug without needing valid Telegram data
     */
    @PostMapping("/debug-auth")
    public Map<String, Object> debugAuth(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String initData = request.get("initData");
            
            log.info("=== DEBUG: Received initData ===");
            log.info("Raw initData: {}", initData);
            
            // Try to parse it (will fail validation but we can see what's being parsed)
            try {
                TelegramAuthService.TelegramUserData userData = 
                    telegramAuthService.validateAndExtractUser(initData);
                
                response.put("success", true);
                response.put("telegramId", userData.getTelegramId());
                response.put("firstName", userData.getFirstName());
                response.put("lastName", userData.getLastName());
                response.put("username", userData.getUsername());
                response.put("languageCode", userData.getLanguageCode());
                response.put("isPremium", userData.getIsPremium());
                response.put("message", "✅ Successfully validated and parsed Telegram data!");
                
            } catch (SecurityException e) {
                response.put("success", false);
                response.put("validationError", e.getMessage());
                response.put("message", "❌ Validation failed (expected if using sample data). This is normal for testing.");
                response.put("note", "For real testing, you need initData from Telegram with a valid hash");
            }
            
        } catch (Exception e) {
            log.error("Debug auth error", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
        }
        
        return response;
    }

    /**
     * Show configuration status
     */
    @GetMapping("/config")
    public Map<String, Object> checkConfig() {
        Map<String, Object> response = new HashMap<>();
        
        // Note: Don't expose actual bot token in production!
        response.put("message", "Configuration check");
        response.put("hasBotToken", telegramAuthService != null);
        response.put("instructions", new String[]{
            "1. Make sure telegram.bot.token is set in application.properties",
            "2. Get real initData from Telegram Mini App",
            "3. Use /api/telegram/auth endpoint with real initData",
            "4. Remove this test controller in production!"
        });
        
        return response;
    }
}
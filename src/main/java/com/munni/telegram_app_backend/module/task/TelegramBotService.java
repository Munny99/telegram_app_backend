package com.munni.telegram_app_backend.module.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service to verify Telegram channel membership using Telegram Bot API
 */
@Slf4j
@Service
public class TelegramBotService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    /**
     * Check if user is a member of a Telegram channel
     */
    public boolean isUserMemberOfChannel(String userId, String channelUsername) {
        if (botToken == null || botToken.isEmpty()) {
            log.error("❌ TELEGRAM BOT TOKEN NOT CONFIGURED!");
            log.error("Please add 'telegram.bot.token=YOUR_TOKEN' to application.properties");
            return false;
        }

        try {
            // Clean channel username - remove @ if present
            String cleanChannel = channelUsername.startsWith("@") ? channelUsername : "@" + channelUsername;

            String url = String.format("%s%s/getChatMember?chat_id=%s&user_id=%s",
                    TELEGRAM_API_URL, botToken, cleanChannel, userId);

            log.info("🔍 Checking membership:");
            log.info("   User ID: {}", userId);
            log.info("   Channel: {}", cleanChannel);
            log.info("   API URL: {}", url.replace(botToken, "***TOKEN***"));

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            log.info("📥 API Response: {}", response);

            if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                String status = (String) result.get("status");

                log.info("📊 User Status: {}", status);

                // User is a member if status is: creator, administrator, member
                boolean isMember = "creator".equals(status) ||
                        "administrator".equals(status) ||
                        "member".equals(status);

                if (isMember) {
                    log.info("✅ User {} IS a member of {} (status: {})", userId, cleanChannel, status);
                } else {
                    log.warn("❌ User {} is NOT a member of {} (status: {})", userId, cleanChannel, status);
                }

                return isMember;
            } else {
                log.error("❌ API returned ok=false: {}", response);
                return false;
            }

        } catch (HttpClientErrorException.BadRequest e) {
            log.error("❌ Bad Request (400) - Possible reasons:");
            log.error("   1. User is not a member of the channel");
            log.error("   2. Bot is not added as admin to the channel");
            log.error("   3. Channel username is incorrect");
            log.error("   Error details: {}", e.getResponseBodyAsString());
            return false;

        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("❌ Unauthorized (401) - Bot token is invalid!");
            log.error("   Please check your telegram.bot.token in application.properties");
            log.error("   Error: {}", e.getResponseBodyAsString());
            return false;

        } catch (HttpClientErrorException.Forbidden e) {
            log.error("❌ Forbidden (403) - Bot doesn't have access to this chat");
            log.error("   Please add the bot as ADMINISTRATOR to channel: {}", channelUsername);
            log.error("   Error: {}", e.getResponseBodyAsString());
            return false;

        } catch (HttpClientErrorException e) {
            log.error("❌ HTTP Error ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            return false;

        } catch (Exception e) {
            log.error("❌ Unexpected error checking channel membership", e);
            return false;
        }
    }

    /**
     * Extract channel username from Telegram URL
     */
    public String extractChannelUsername(String taskLink) {
        if (taskLink == null || taskLink.isEmpty()) {
            log.warn("⚠️ Task link is null or empty");
            return null;
        }

        log.info("🔗 Extracting channel from link: {}", taskLink);

        // Pattern to match t.me links
        Pattern pattern = Pattern.compile("(?:https?://)?(?:www\\.)?t\\.me/([a-zA-Z0-9_]+)");
        Matcher matcher = pattern.matcher(taskLink);

        if (matcher.find()) {
            String username = matcher.group(1);
            log.info("✅ Extracted username: {}", username);
            return username;
        }

        log.error("❌ Could not extract channel username from link: {}", taskLink);
        return null;
    }

    /**
     * Test bot configuration and access
     */
    public BotStatus testBotConfiguration() {
        BotStatus status = new BotStatus();

        if (botToken == null || botToken.isEmpty()) {
            status.configured = false;
            status.message = "Bot token not configured in application.properties";
            log.error("❌ {}", status.message);
            return status;
        }

        status.configured = true;
        status.token = botToken.substring(0, Math.min(10, botToken.length())) + "...";

        try {
            String url = String.format("%s%s/getMe", TELEGRAM_API_URL, botToken);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                status.working = true;
                status.botUsername = (String) result.get("username");
                status.botName = (String) result.get("first_name");
                status.message = "Bot is working correctly";

                log.info("✅ Bot Configuration:");
                log.info("   Bot Name: {}", status.botName);
                log.info("   Bot Username: @{}", status.botUsername);
                log.info("   Token: {}...", botToken.substring(0, 10));
            } else {
                status.working = false;
                status.message = "Bot token is invalid";
                log.error("❌ {}", status.message);
            }
        } catch (Exception e) {
            status.working = false;
            status.message = "Error testing bot: " + e.getMessage();
            log.error("❌ {}", status.message, e);
        }

        return status;
    }

    /**
     * Verify if bot has access to a specific channel
     */
    public ChannelAccessStatus testChannelAccess(String channelUsername) {
        ChannelAccessStatus status = new ChannelAccessStatus();
        status.channelUsername = channelUsername;

        if (botToken == null || botToken.isEmpty()) {
            status.hasAccess = false;
            status.message = "Bot token not configured";
            return status;
        }

        try {
            String cleanChannel = channelUsername.startsWith("@") ? channelUsername : "@" + channelUsername;
            String url = String.format("%s%s/getChat?chat_id=%s",
                    TELEGRAM_API_URL, botToken, cleanChannel);

            log.info("🔍 Testing bot access to channel: {}", cleanChannel);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                status.hasAccess = true;
                status.channelTitle = (String) result.get("title");
                status.channelType = (String) result.get("type");
                status.message = "Bot has access to this channel";

                log.info("✅ Channel Access:");
                log.info("   Channel: {}", status.channelTitle);
                log.info("   Type: {}", status.channelType);
                log.info("   Username: {}", cleanChannel);
            } else {
                status.hasAccess = false;
                status.message = "Bot cannot access this channel";
                log.error("❌ {}", status.message);
            }

        } catch (HttpClientErrorException.Forbidden e) {
            status.hasAccess = false;
            status.message = "Bot is not added as administrator to this channel";
            log.error("❌ {}", status.message);

        } catch (Exception e) {
            status.hasAccess = false;
            status.message = "Error: " + e.getMessage();
            log.error("❌ Error testing channel access", e);
        }

        return status;
    }

    // Status classes for debugging
    @lombok.Data
    public static class BotStatus {
        private boolean configured = false;
        private boolean working = false;
        private String token;
        private String botUsername;
        private String botName;
        private String message;
    }

    @lombok.Data
    public static class ChannelAccessStatus {
        private String channelUsername;
        private boolean hasAccess = false;
        private String channelTitle;
        private String channelType;
        private String message;
    }
}
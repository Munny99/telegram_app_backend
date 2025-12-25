package com.munni.telegram_app_backend.module.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

/**
 * Service for verifying membership in PRIVATE channels with manual approval
 */
@Slf4j
@Service
public class TelegramManualApprovalService {

    @Value("${telegram.bot.token:}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    /**
     * Check if user is member of a PRIVATE channel (using chat ID)
     * @param chatId Channel ID (e.g., "-1001234567890")
     * @param userId User's Telegram ID
     * @return true if user is a member
     */
    public boolean isUserMemberOfPrivateChannel(String chatId, String userId) {
        if (botToken == null || botToken.isEmpty()) {
            log.error("❌ Bot token not configured");
            return false;
        }

        try {
            String url = String.format("%s%s/getChatMember?chat_id=%s&user_id=%s",
                    TELEGRAM_API_URL, botToken, chatId, userId);

            log.info("🔍 Checking private channel membership:");
            log.info("   Chat ID: {}", chatId);
            log.info("   User ID: {}", userId);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            log.info("📥 API Response: {}", response);

            if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                String status = (String) result.get("status");

                log.info("📊 User Status: {}", status);

                boolean isMember = "creator".equals(status) || 
                                  "administrator".equals(status) || 
                                  "member".equals(status);

                if (isMember) {
                    log.info("✅ User {} IS a member of private channel {}", userId, chatId);
                } else {
                    log.warn("❌ User {} is NOT a member (status: {})", userId, status);
                }

                return isMember;
            }

            return false;

        } catch (HttpClientErrorException.BadRequest e) {
            log.error("❌ Bad Request - User not member or not approved yet: {}", e.getResponseBodyAsString());
            return false;
            
        } catch (Exception e) {
            log.error("❌ Error checking private channel membership", e);
            return false;
        }
    }

    /**
     * Get channel info to verify bot has access
     * @param chatId Channel ID
     * @return Channel information or null
     */
    public ChannelInfo getChannelInfo(String chatId) {
        if (botToken == null || botToken.isEmpty()) {
            return null;
        }

        try {
            String url = String.format("%s%s/getChat?chat_id=%s",
                    TELEGRAM_API_URL, botToken, chatId);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                
                ChannelInfo info = new ChannelInfo();
                info.setId(result.get("id").toString());
                info.setTitle((String) result.get("title"));
                info.setType((String) result.get("type"));
                info.setUsername((String) result.get("username"));
                
                Object inviteLink = result.get("invite_link");
                if (inviteLink != null) {
                    info.setInviteLink(inviteLink.toString());
                }
                
                log.info("✅ Channel Info: {}", info);
                return info;
            }

            return null;

        } catch (Exception e) {
            log.error("❌ Error getting channel info", e);
            return null;
        }
    }

    /**
     * Extract chat ID from invite link
     * Note: For private channels, you need to store the chat ID when creating the task
     * Invite links like https://t.me/+ABC123 don't contain the chat ID
     */
    public String extractChatIdFromInviteLink(String inviteLink) {
        // Private invite links don't contain chat ID
        // You must store chat ID separately in the database
        log.warn("⚠️ Cannot extract chat ID from private invite link: {}", inviteLink);
        log.warn("⚠️ You must store the chat ID in database when creating the task");
        return null;
    }

    @lombok.Data
    public static class ChannelInfo {
        private String id;
        private String title;
        private String type;
        private String username;
        private String inviteLink;
    }
}
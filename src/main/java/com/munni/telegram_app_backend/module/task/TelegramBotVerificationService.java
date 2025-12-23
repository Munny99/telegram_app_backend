package com.munni.telegram_app_backend.module.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Service to verify Telegram channel/group subscriptions using Telegram Bot API
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotVerificationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${telegram.bot.token}")
    private String botToken;

    /**
     * Check if a user is a member of a Telegram channel/group
     * 
     * @param userId Telegram user ID
     * @param channelUsername Channel username (e.g., "@yourchannel") or chat ID
     * @return true if user is a member, false otherwise
     */
    public boolean isUserMemberOfChannel(Long userId, String channelUsername) {
        try {
            String url = String.format(
                "https://api.telegram.org/bot%s/getChatMember?chat_id=%s&user_id=%d",
                botToken,
                channelUsername,
                userId
            );

            log.info("Checking if user {} is member of channel {}", userId, channelUsername);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Boolean ok = (Boolean) body.get("ok");

                if (Boolean.TRUE.equals(ok)) {
                    Map<String, Object> result = (Map<String, Object>) body.get("result");
                    String status = (String) result.get("status");

                    // Valid member statuses: "creator", "administrator", "member"
                    // Invalid statuses: "left", "kicked"
                    boolean isMember = "creator".equals(status) 
                                    || "administrator".equals(status) 
                                    || "member".equals(status);

                    log.info("User {} membership status in {}: {} (isMember: {})", 
                            userId, channelUsername, status, isMember);

                    return isMember;
                }
            }

            log.warn("Failed to verify user {} membership in {}: Invalid response", 
                    userId, channelUsername);
            return false;

        } catch (Exception e) {
            log.error("Error checking membership for user {} in channel {}: {}", 
                    userId, channelUsername, e.getMessage());
            return false;
        }
    }

    /**
     * Check if a user is subscribed to a YouTube channel
     * Note: This requires YouTube Data API v3 integration
     * 
     * @param userId User ID (not directly applicable for YouTube)
     * @param channelId YouTube channel ID or handle
     * @return true if subscribed (currently returns false - needs implementation)
     */
    public boolean isUserSubscribedToYouTube(Long userId, String channelId) {
        // TODO: Implement YouTube API verification
        // This requires:
        // 1. YouTube Data API v3 credentials
        // 2. OAuth2 authentication for the user
        // 3. Checking subscriptions via API
        log.warn("YouTube verification not implemented yet for user {}", userId);
        return false;
    }

    /**
     * Verify custom link task completion
     * This could involve checking if user clicked a tracking link
     * 
     * @param userId User ID
     * @param taskId Task ID
     * @return true if verified
     */
    public boolean verifyCustomLink(Long userId, Long taskId) {
        // TODO: Implement custom link verification
        // Options:
        // 1. Use tracking pixels
        // 2. Require users to enter a code found on the destination page
        // 3. Use webhook callbacks from the destination
        log.warn("Custom link verification not implemented yet for user {}", userId);
        return false;
    }
}
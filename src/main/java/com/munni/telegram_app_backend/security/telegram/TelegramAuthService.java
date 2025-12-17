package com.munni.telegram_app_backend.security.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.munni.telegram_app_backend.module.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@Service
public class TelegramAuthService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Validates Telegram initData and extracts user information
     */
    public TelegramUserData validateAndExtractUser(String initData) throws Exception {
        Map<String, String> params = parseInitData(initData);

        if (!validateInitData(params)) {
            throw new SecurityException("Invalid Telegram authentication data");
        }

        return extractUserData(params);
    }

    /**
     * Parse initData string into key-value pairs
     */
    private Map<String, String> parseInitData(String initData) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        String[] pairs = initData.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }

        log.debug("Parsed initData parameters: {}", params.keySet());
        return params;
    }

    /**
     * Validate the authenticity of Telegram initData
     */
    private boolean validateInitData(Map<String, String> params) throws Exception {
        String receivedHash = params.get("hash");
        if (receivedHash == null || receivedHash.isEmpty()) {
            log.error("Hash parameter is missing from initData");
            return false;
        }

        // Check auth_date to prevent replay attacks (optional but recommended)
        String authDateStr = params.get("auth_date");
        if (authDateStr != null) {
            long authDate = Long.parseLong(authDateStr);
            long currentTime = System.currentTimeMillis() / 1000;
            // Reject if auth_date is older than 24 hours
            if (currentTime - authDate > 86400) {
                log.error("Auth date is too old: {} seconds ago", currentTime - authDate);
                return false;
            }
        }

        // Create data check string (all params except hash, sorted alphabetically)
        List<String> dataCheckArr = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!entry.getKey().equals("hash")) {
                dataCheckArr.add(entry.getKey() + "=" + entry.getValue());
            }
        }
        Collections.sort(dataCheckArr);
        String dataCheckString = String.join("\n", dataCheckArr);

        log.debug("Data check string: {}", dataCheckString);

        // Create secret key: HMAC-SHA256(bot_token, "WebAppData")
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                "WebAppData".getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        hmacSha256.init(secretKeySpec);
        byte[] secretKey = hmacSha256.doFinal(botToken.getBytes(StandardCharsets.UTF_8));

        // Calculate hash: HMAC-SHA256(data_check_string, secret_key)
        hmacSha256 = Mac.getInstance("HmacSHA256");
        secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
        hmacSha256.init(secretKeySpec);
        byte[] calculatedHash = hmacSha256.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

        // Convert to hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : calculatedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        String calculatedHashStr = hexString.toString();
        boolean isValid = calculatedHashStr.equals(receivedHash);

        if (!isValid) {
            log.error("Hash validation failed. Expected: {}, Received: {}", calculatedHashStr, receivedHash);
        }

        return isValid;
    }

    /**
     * Extract user data from validated params
     */
    private TelegramUserData extractUserData(Map<String, String> params) throws Exception {
        String userJson = params.get("user");
        if (userJson == null || userJson.isEmpty()) {
            throw new IllegalArgumentException("User data not found in initData");
        }

        log.debug("Parsing user JSON: {}", userJson);
        JsonNode userNode = objectMapper.readTree(userJson);

        TelegramUserData userData = new TelegramUserData();
        userData.setTelegramId(userNode.get("id").asText());
        userData.setFirstName(userNode.has("first_name") ? userNode.get("first_name").asText() : null);
        userData.setLastName(userNode.has("last_name") ? userNode.get("last_name").asText() : null);
        userData.setUsername(userNode.has("username") ? userNode.get("username").asText() : null);
        userData.setLanguageCode(userNode.has("language_code") ? userNode.get("language_code").asText() : null);
        userData.setIsPremium(userNode.has("is_premium") && userNode.get("is_premium").asBoolean());
        userData.setPhotoUrl(userNode.has("photo_url") ? userNode.get("photo_url").asText() : null);

        // Additional fields from initData
        userData.setAuthDate(params.get("auth_date"));
        userData.setQueryId(params.get("query_id"));

        log.info("Successfully extracted user data for Telegram ID: {}", userData.getTelegramId());
        return userData;
    }

    /**
     * DTO for Telegram user data
     */
    public static class TelegramUserData {
        private String telegramId;
        private String firstName;
        private String lastName;
        private String username;
        private String languageCode;
        private Boolean isPremium;
        private String photoUrl;
        private String authDate;
        private String queryId;

        // Getters and Setters
        public String getTelegramId() { return telegramId; }
        public void setTelegramId(String telegramId) { this.telegramId = telegramId; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getLanguageCode() { return languageCode; }
        public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

        public Boolean getIsPremium() { return isPremium; }
        public void setIsPremium(Boolean isPremium) { this.isPremium = isPremium; }

        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

        public String getAuthDate() { return authDate; }
        public void setAuthDate(String authDate) { this.authDate = authDate; }

        public String getQueryId() { return queryId; }
        public void setQueryId(String queryId) { this.queryId = queryId; }
    }
}
package com.munni.telegram_app_backend.model;

import com.munni.telegram_app_backend.module.user.User;
import com.munni.telegram_app_backend.module.user.UserRepo;
import com.munni.telegram_app_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MyTelegramBot extends TelegramLongPollingBot {

    @Autowired private UserRepo userRepository;
    @Autowired  private JwtService jwtService;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        System.out.println("Update received: " + update);
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Message message = update.getMessage();
        org.telegram.telegrambots.meta.api.objects.User tgUser = message.getFrom();

        String telegramId = String.valueOf(tgUser.getId());

        // 🔐 LOGIN OR REGISTER
        User appUser = userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> registerNewUser(tgUser));

        // 🔑 GENERATE JWT
        String accessToken = jwtService.generateToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);
        System.out.println("Update received: " + update);

        sendReply(
                message.getChatId(),
                """
                ✅ Login successful!

                👤 User: %s
                🆔 Telegram ID: %s

                🔐 Access Token:
                %s

                🔁 Refresh Token:
                %s

                ⚠️ Do not share your token with anyone.
                """.formatted(
                        appUser.getFirstName(),
                        telegramId,
                        accessToken,
                        refreshToken
                )
        );
    }

    /**
     * Register new Telegram user
     */
    private User registerNewUser(org.telegram.telegrambots.meta.api.objects.User tgUser) {

        return userRepository.save(
                User.builder()
                        .telegramId(String.valueOf(tgUser.getId()))
                        .firstName(tgUser.getFirstName())
                        .lastName(tgUser.getLastName() != null ? tgUser.getLastName() : "")
                        .userName(tgUser.getUserName())
                        .password(null) // Telegram login → no password
                        .referralCode(generateReferralCode())
                        .totalEarnings(BigDecimal.ZERO)
                        .totalWithdrawn(BigDecimal.valueOf(0L))
                        .pendingBalance(0L)
                        .totalReferrals(0)
                        .completedTasks(0)
                        .totalTasks(0)
                        .isActive(true)
                        .build()
        );
    }

    private String generateReferralCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

    private void sendReply(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

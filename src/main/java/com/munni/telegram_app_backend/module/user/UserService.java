package com.munni.telegram_app_backend.module.user;


import com.munni.telegram_app_backend.security.telegram.TelegramAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepo userRepository;

	/**
	 * Find user by Telegram ID
	 */
	public Optional<User> findByTelegramId(String telegramId) {
		return userRepository.findByTelegramId(telegramId);
	}

	/**
	 * Find or create user from Telegram data
	 */
	@Transactional
	public User findOrCreateUser(TelegramAuthService.TelegramUserData telegramData) {
		return userRepository.findByTelegramId(telegramData.getTelegramId())
				.orElseGet(() -> createNewUser(telegramData));
	}

	/**
	 * Create a new user from Telegram data
	 */
	private User createNewUser(TelegramAuthService.TelegramUserData telegramData) {
		log.info("Creating new user for Telegram ID: {}", telegramData.getTelegramId());

		User user = User.builder()
				.telegramId(telegramData.getTelegramId())
				.firstName(telegramData.getFirstName())
				.lastName(telegramData.getLastName() != null ? telegramData.getLastName() : "")
				.userName(telegramData.getUsername() != null ? telegramData.getUsername() : generateUsername(telegramData))
				.email(generateEmail(telegramData))
				.referralCode(generateReferralCode())
				.totalEarnings(BigDecimal.ZERO)
				.totalWithdrawn(0L)
				.pendingBalance(0L)
				.totalReferrals(0)
				.completedTasks(0)
				.totalTasks(0)
				.rating(0)
				.isActive(true)
				.build();

		return userRepository.save(user);
	}

	/**
	 * Update existing user data from Telegram
	 */
	@Transactional
	public User updateUserFromTelegram(User user, TelegramAuthService.TelegramUserData telegramData) {
		boolean updated = false;

		// Update fields if they've changed in Telegram
		if (telegramData.getFirstName() != null && !telegramData.getFirstName().equals(user.getFirstName())) {
			user.setFirstName(telegramData.getFirstName());
			updated = true;
		}

		if (telegramData.getLastName() != null && !telegramData.getLastName().equals(user.getLastName())) {
			user.setLastName(telegramData.getLastName());
			updated = true;
		}

		if (telegramData.getUsername() != null && !telegramData.getUsername().equals(user.getUsername())) {
			user.setUserName(telegramData.getUsername());
			updated = true;
		}

		if (updated) {
			log.info("Updated user data for Telegram ID: {}", telegramData.getTelegramId());
			return userRepository.save(user);
		}

		return user;
	}

	/**
	 * Generate a unique referral code
	 */
	private String generateReferralCode() {
		String code;
		do {
			code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		} while (userRepository.existsByReferralCode(code));
		return code;
	}

	/**
	 * Generate username if not provided
	 */
	private String generateUsername(TelegramAuthService.TelegramUserData telegramData) {
		String baseUsername = telegramData.getFirstName().toLowerCase().replaceAll("[^a-z0-9]", "");
		String username = baseUsername;
		int counter = 1;

		while (userRepository.existsByUserName(username)) {
			username = baseUsername + counter++;
		}

		return username;
	}

	/**
	 * Generate email from Telegram ID (since email is required in your entity)
	 */
	private String generateEmail(TelegramAuthService.TelegramUserData telegramData) {
		// Generate a placeholder email using Telegram ID
		return "telegram_" + telegramData.getTelegramId() + "@telegram.temp";
	}
}
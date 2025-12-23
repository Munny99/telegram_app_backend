package com.munni.telegram_app_backend.module.user;


//import com.munni.telegram_app_backend.module.task.TaskInitializationService;
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
//	private final TaskInitializationService taskInitializationService;

	@Transactional
	public User findOrCreateUser(TelegramAuthService.TelegramUserData telegramData) {

		Optional<User> existingUser =
				userRepository.findByTelegramId(telegramData.getTelegramId());

		if (existingUser.isPresent()) {
			log.info("User found: {}", telegramData.getTelegramId());
			return existingUser.get();
		}

		String username = telegramData.getUsername();
		if (username == null || username.isBlank()) {
			username = generateUsername(telegramData);
		}

		User newUser = User.builder()
				.telegramId(telegramData.getTelegramId())
				.firstName(telegramData.getFirstName())
				.lastName(
						telegramData.getLastName() != null
								? telegramData.getLastName()
								: ""
				)
				.userName(username)   // ✅ never null now
				.referralCode(generateReferralCode())
				.totalEarnings(BigDecimal.ZERO)
				.totalWithdrawn(BigDecimal.ZERO)
				.pendingBalance(0L)
				.totalReferrals(0)
				.completedTasks(0)
				.totalTasks(0)
				.rating(0.0)
				.isActive(true)
				.build();

		User savedUser = userRepository.save(newUser);
		log.info("New user created: {}", savedUser.getTelegramId());

//		taskInitializationService.initializeTasksForNewUser(savedUser);

		savedUser.setTotalTasks(20);
		return userRepository.save(savedUser);
	}


	/**
	 * Update user information from Telegram data
	 */
	@Transactional
	public User updateUserFromTelegram(User user, TelegramAuthService.TelegramUserData telegramData) {
		boolean updated = false;

		if (telegramData.getFirstName() != null &&
				!telegramData.getFirstName().equals(user.getFirstName())) {
			user.setFirstName(telegramData.getFirstName());
			updated = true;
		}

		if (telegramData.getLastName() != null &&
				!telegramData.getLastName().equals(user.getLastName())) {
			user.setLastName(telegramData.getLastName());
			updated = true;
		}

		if (telegramData.getUsername() != null &&
				!telegramData.getUsername().equals(user.getUsername())) {
			user.setUserName(telegramData.getUsername());
			updated = true;
		}

		if (updated) {
			user = userRepository.save(user);
			log.info("User information updated: {}", user.getTelegramId());
		}

		return user;
	}

	public Optional<User> findByTelegramId(String telegramId) {
		return userRepository.findByTelegramId(telegramId);
	}

	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	public Optional<User> findByReferralCode(String referralCode) {
		return userRepository.findByReferralCode(referralCode);
	}

	/**
	 * Process withdrawal
	 */
	@Transactional
	public User processWithdrawal(Long userId, BigDecimal amount) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		Long amountInCents = amount.multiply(new BigDecimal(100)).longValue();

		if (user.getPendingBalance() < amountInCents) {
			throw new RuntimeException("Insufficient balance");
		}

		user.setPendingBalance(user.getPendingBalance() - amountInCents);
		user.setTotalWithdrawn(user.getTotalWithdrawn().add(amount));

		return userRepository.save(user);
	}

	/**
	 * Process referral
	 */
	@Transactional
	public User processReferral(Long userId, String referralCode) {
		User referrer = userRepository.findByReferralCode(referralCode)
				.orElseThrow(() -> new RuntimeException("Invalid referral code"));

		if (referrer.getId().equals(userId)) {
			throw new RuntimeException("Cannot refer yourself");
		}

		referrer.setTotalReferrals(referrer.getTotalReferrals() + 1);

		BigDecimal referralBonus = new BigDecimal("5.00");
		referrer.setTotalEarnings(referrer.getTotalEarnings().add(referralBonus));
		Long bonusInCents = referralBonus.multiply(new BigDecimal(100)).longValue();
		referrer.setPendingBalance(referrer.getPendingBalance() + bonusInCents);

		return userRepository.save(referrer);
	}

	private String generateReferralCode() {
		String code;
		do {
			code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		} while (userRepository.findByReferralCode(code).isPresent());
		return code;
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
				.referralCode(generateReferralCode())
				.totalEarnings(BigDecimal.ZERO)
				.totalWithdrawn(BigDecimal.valueOf(0L))
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



	/**
	 * Update user balance after task completion
	 */
	@Transactional
	public User updateBalanceAfterTask(Long userId, BigDecimal reward) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setTotalEarnings(user.getTotalEarnings().add(reward));

		// Convert reward to cents for pending balance
		Long rewardInCents = reward.multiply(new BigDecimal(100)).longValue();
		user.setPendingBalance(user.getPendingBalance() + rewardInCents);

		user.setCompletedTasks(user.getCompletedTasks() + 1);

		return userRepository.save(user);
	}



	/**
	 * Get user statistics
	 */
	public UserStatistics getUserStatistics(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		return UserStatistics.builder()
				.totalUsers(userRepository.count())
				.userEarnings(user.getTotalEarnings())
				.userRank(null)
				.completionRate(calculateCompletionRate(user))
				.build();
	}

//	private Long calculateUserRank(User user) {
//		return userRepository.countByTotalEarningsGreaterThan(user.getTotalEarnings()) + 1;
//	}

	private Double calculateCompletionRate(User user) {
		if (user.getTotalTasks() == 0) {
			return 0.0;
		}
		return (double) user.getCompletedTasks() / user.getTotalTasks() * 100;
	}

	@lombok.Builder
	@lombok.Data
	public static class UserStatistics {
		private Long totalUsers;
		private BigDecimal userEarnings;
		private Long userRank;
		private Double completionRate;
	}
}
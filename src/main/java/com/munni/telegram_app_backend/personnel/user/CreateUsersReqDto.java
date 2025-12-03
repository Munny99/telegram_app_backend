package com.munni.telegram_app_backend.personnel.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Zubayer Ahamed
 * @since Jul 2, 2025
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUsersReqDto {

	private String firstName;
	private String lastName;
	private String userName;
	private String referralCode;

	private String telegramId;


	public User getBean() {
		return User.builder()

				.firstName(firstName)
				.lastName(lastName)
				.userName(userName)
				.telegramId(telegramId)
				.referralCode(referralCode)
				.build();
	}
}

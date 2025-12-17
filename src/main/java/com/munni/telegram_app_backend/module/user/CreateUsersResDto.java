package com.munni.telegram_app_backend.module.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * Zubayer Ahamed
 * @since Jul 2, 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUsersResDto {

	private String firstName;
	private String lastName;
	private String userName;
	private String referralCode;

	private String telegramId;


	public CreateUsersResDto(User users) {
		BeanUtils.copyProperties(users, this);
	}
}

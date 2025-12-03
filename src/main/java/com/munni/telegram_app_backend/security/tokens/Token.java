package com.munni.telegram_app_backend.security.tokens;


import com.munni.telegram_app_backend.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tokens")
public class Token implements Serializable {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String token;

	@Enumerated(EnumType.STRING)
	private TokenType xtype;

	@Column(name = "is_revoked")
	private boolean revoked;

	@Column(name = "is_expired")
	private boolean expired;

	@Column(name = "user_id")
	private Long userId;
}

package com.munni.telegram_app_backend.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.munni.telegram_app_backend.exception.CustomException;
import com.munni.telegram_app_backend.personnel.user.User;
import com.munni.telegram_app_backend.personnel.user.UserRepo;
import com.munni.telegram_app_backend.personnel.user.UserService;
import com.munni.telegram_app_backend.security.JwtService;
import com.munni.telegram_app_backend.security.UserDetailsImpl;
import com.munni.telegram_app_backend.security.tokens.TokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {

	@Autowired private UserRepo userRepo;
	@Autowired private JwtService jwtService;
	@Autowired PasswordEncoder passwordEncoder;
	@Autowired private TokenRepo tokensRepo;
	@Autowired private UserService userService;

	// ===============================================================
	// REGISTER
	// ===============================================================
	@Transactional
	public AuthenticationResDto register(RegisterRequestDto request) {
		if (userRepo.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
			throw new CustomException("Email is already registered.", HttpStatus.BAD_REQUEST);
		} else if (userRepo.findByUserNameIgnoreCase(request.getUserName()).isPresent()) {
			throw new CustomException("Username is already registered.", HttpStatus.BAD_REQUEST);
		}

		// 2. Create user
		User user = User.builder()
				.firstName(request.getFullName())
				.telegramId(request.getEmail())
				.userName(request.getUserName())
				.isActive(Boolean.TRUE)
				.build();

		user = userRepo.save(user);

		// Wrap UserDetails
		UserDetailsImpl userDetails = new UserDetailsImpl(user);

		// 3. Add ROLE ID inside JWT (IMPORTANT)
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("roleId", user.getRole().getId());
		extraClaims.put("userId", user.getId());

		// 4. Generate tokens
		var jwtToken = jwtService.generateToken(extraClaims, userDetails);
		var refreshToken = jwtService.generateRefreshToken(userDetails);

		// 5. Save token
		saveUserToken(user.getId(), jwtToken);

		return AuthenticationResDto.builder()
				.accessToken(jwtToken)
				.refreshToken(refreshToken)
				.build();
	}

	// ===============================================================
	// AUTHENTICATE (LOGIN)
	// ===============================================================
	@Transactional
	public AuthenticationResDto authenticate(AuthenticationReqDto request) {

		// 1. Find user by username or email
		Optional<User> userOp = userRepo.findByUserNameIgnoreCase(request.getLogin());
		if (userOp.isEmpty()) userOp = userRepo.findByEmailIgnoreCase(request.getLogin());

		User user = userOp.orElseThrow(() ->
				new RuntimeException("User not found with provided username/email.")
		);

		// 2. Check active status
		if (Boolean.FALSE.equals(user.getIsActive())) {
			throw new RuntimeException("User account is inactive.");
		}

		// 3. Verify password
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid credentials.");
		}

		// Convert to UserDetails
		UserDetailsImpl userDetails = new UserDetailsImpl(user);

		// 4. Add ROLE ID inside JWT (IMPORTANT)
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("roleId", user.getRole().getId());
		extraClaims.put("userId", user.getId());

		// 5. Generate tokens
		var jwtToken = jwtService.generateToken(extraClaims, userDetails);
		var refreshToken = jwtService.generateRefreshToken(userDetails);

		// 6. Revoke old tokens
		revokeAllUserTokens(user.getId());
		saveUserToken(user.getId(), jwtToken);

		return AuthenticationResDto.builder()
				.accessToken(jwtToken)
				.refreshToken(refreshToken)
				.build();
	}

	// ===============================================================
	// TOKEN HELPERS
	// ===============================================================
	@Transactional
	void revokeAllUserTokens(Long userId) {
		List<Token> validTokens = tokensRepo.findAllByUserIdAndRevokedAndExpired(userId, false, false);
		if (validTokens.isEmpty()) return;

		validTokens.forEach(t -> {
			t.setRevoked(true);
			t.setExpired(true);
		});

		tokensRepo.saveAll(validTokens);
	}

	@Transactional
	void saveUserToken(Long userId, String jwtToken) {
		Token xtoken = Token.builder()
				.userId(userId)
				.token(jwtToken)
				.revoked(false)
				.expired(false)
				.xtype(TokenType.BEARER)
				.build();

		tokensRepo.save(xtoken);
	}

	// ===============================================================
	// REFRESH TOKEN
	// ===============================================================
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}

		final String refreshToken = authHeader.substring(7);
		final String usernameOrEmail = jwtService.extractUsername(refreshToken);

		if (StringUtils.isNotBlank(usernameOrEmail)) {
			UserDetailsImpl userDetails =
					(UserDetailsImpl) userService.loadUserByUsername(usernameOrEmail);

			if (jwtService.isTokenValid(refreshToken, userDetails)) {

				// IMPORTANT: when refreshing, also include role Id
				Map<String, Object> extraClaims = new HashMap<>();
				extraClaims.put("roleId", userDetails.getUser().getRole().getId());

				String accessToken = jwtService.generateToken(extraClaims, userDetails);
				Long userId = userDetails.getUser().getId();

				revokeAllUserTokens(userId);
				saveUserToken(userId, accessToken);

				AuthenticationResDto authResponse = AuthenticationResDto.builder()
						.accessToken(accessToken)
						.refreshToken(refreshToken)
						.build();

				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			}
		}
	}

}

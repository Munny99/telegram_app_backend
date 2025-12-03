package com.munni.telegram_app_backend.security.tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {

	List<Token> findAllByUserIdAndRevokedAndExpired(Long userId, boolean revoked, boolean expired);
	Optional<Token> findByToken(String token);
}

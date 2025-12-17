package com.munni.telegram_app_backend.security;

import com.munni.telegram_app_backend.exception.CustomAccessDeniedHandler;
import com.munni.telegram_app_backend.exception.CustomAuthenticationEntryPoint;
import com.munni.telegram_app_backend.module.user.UserService;
import com.munni.telegram_app_backend.security.auth.LogoutService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.Security;
import java.util.Arrays;
import java.util.List;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable()) // Disable CSRF for API
				.cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless for API
				.authorizeHttpRequests(auth -> auth
						// Allow all Telegram API endpoints
						.requestMatchers("/api/telegram/**").permitAll()

						// Allow static resources and frontend
						.requestMatchers("/", "/index.html", "/static/**", "/**/*.js", "/**/*.css", "/**/*.ico").permitAll()

						// Allow actuator/health endpoints
						.requestMatchers("/actuator/**", "/health/**").permitAll()

						// Allow error endpoints
						.requestMatchers("/error/**").permitAll()

						// All other endpoints require authentication
						.anyRequest().authenticated()
				);

		return http.build();
	}


	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// Allow requests from Telegram and your frontend
		configuration.setAllowedOrigins(Arrays.asList(
				"https://web.telegram.org",
				"http://localhost:3000",
				"http://localhost:8080",
				"http://localhost:9092",
				"*" // For development only - remove in production
		));

		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(false); // Set to false when using "*" origin
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
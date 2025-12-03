package com.munni.telegram_app_backend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

		ErrorResponse error = ErrorResponse.builder()
				.status(HttpStatus.FORBIDDEN.value()).error("Forbidden")
				.message("You do not have permission to access this resource")
				.build();

		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType("application/json");
		mapper.writeValue(response.getWriter(), error);
	}
}

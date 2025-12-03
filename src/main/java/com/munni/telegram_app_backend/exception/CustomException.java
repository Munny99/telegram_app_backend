package com.munni.telegram_app_backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

public class CustomException extends RuntimeException {

	private final HttpStatus status;

	public CustomException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}

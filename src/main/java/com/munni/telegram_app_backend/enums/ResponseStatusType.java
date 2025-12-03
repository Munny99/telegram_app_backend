package com.munni.telegram_app_backend.enums;

import org.springframework.http.HttpStatus;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

public enum ResponseStatusType {

	CREATE_SUCCESS(HttpStatus.CREATED, "Resource created successfully"),
	READ_SUCCESS(HttpStatus.OK, "Data fetched successfully"),
	UPDATE_SUCCESS(HttpStatus.OK, "Resource updated successfully"),
	DELETE_SUCCESS(HttpStatus.OK, "Resource deleted successfully"),
	DELETE_NO_CONTENT(HttpStatus.NO_CONTENT, "Resource deleted, no content to return"),
	OPERATION_SUCCESS(HttpStatus.OK, "Operation completed successfully");

	private HttpStatus httpStatus;
	private String message;

	ResponseStatusType(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	public String getMessage() {
		return this.message;
	}
}

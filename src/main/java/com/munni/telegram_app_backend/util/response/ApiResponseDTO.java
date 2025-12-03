package com.munni.telegram_app_backend.util.response;

public class ApiResponseDTO<T> extends BaseApiResponseDTO<T> {

    public ApiResponseDTO(boolean success, String message, T data) {
        super(success, message, data);
    }

    public ApiResponseDTO(boolean success, String message) {
        super(success, message);
    }
}
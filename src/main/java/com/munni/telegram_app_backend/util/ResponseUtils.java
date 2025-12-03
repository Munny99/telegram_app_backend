package com.munni.telegram_app_backend.util;

import com.munni.telegram_app_backend.util.response.ApiErrorResponseDTO;
import com.munni.telegram_app_backend.util.response.ApiResponseDTO;
import com.munni.telegram_app_backend.util.response.ApiResponseWithDataDTO;
import com.munni.telegram_app_backend.util.response.BaseApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseUtils {

    public static <T> ResponseEntity<BaseApiResponseDTO<?>> SuccessResponseWithData(String msg, T data, HttpStatusCode status) {
        ApiResponseWithDataDTO<T> apiResponseWithData = new ApiResponseWithDataDTO<T>(true, msg, data);
        return new ResponseEntity<>(apiResponseWithData, status);
    }
    public static <T> ResponseEntity<BaseApiResponseDTO<?>> SuccessResponseWithData(String msg, T data) {
        ApiResponseWithDataDTO<T> apiResponseWithData = new ApiResponseWithDataDTO<T>(true, msg, data);
        return new ResponseEntity<>(apiResponseWithData, HttpStatus.OK);
    }

    public static <T> ResponseEntity<BaseApiResponseDTO<?>> SuccessResponseWithData(boolean success, String msg, T data, HttpStatus status) {
        ApiResponseWithDataDTO<T> apiResponseWithData = new ApiResponseWithDataDTO<T>(success, msg, data);
        return new ResponseEntity<>(apiResponseWithData, status);
    }
    public static <T> ResponseEntity<BaseApiResponseDTO<?>> SuccessResponseWithData(T data) {
        ApiResponseWithDataDTO<T> apiResponseWithData = new ApiResponseWithDataDTO<T>(true, "Data fetched successfully.", data);
        return new ResponseEntity<>(apiResponseWithData, HttpStatus.OK);
    }

    public static ResponseEntity<BaseApiResponseDTO<?>> SuccessResponse(String msg, HttpStatusCode status) {
        ApiResponseDTO apiResponseWithData = new ApiResponseDTO(true, msg);
        return new ResponseEntity<>(apiResponseWithData, status);
    }
    public static ResponseEntity<BaseApiResponseDTO<?>> SuccessResponse(String msg) {
        ApiResponseDTO apiResponseWithData = new ApiResponseDTO(true, msg);
        return new ResponseEntity<>(apiResponseWithData, HttpStatus.OK);
    }

    public static <T> ResponseEntity<BaseApiResponseDTO<?>> FailedResponse(String msg, HttpStatusCode status) {
        ApiResponseDTO apiResponseWithData = new ApiResponseDTO(false, msg);
        return new ResponseEntity<>(apiResponseWithData, status);
    }

    public static <T> ResponseEntity<BaseApiResponseDTO<?>> FailedResponse(String msg) {
        ApiResponseDTO apiResponseWithData = new ApiResponseDTO(false, msg);
        return new ResponseEntity<>(apiResponseWithData, HttpStatus.OK);
    }


//    // Method 1: Accepts a custom message and detailed errors map
//    public static ResponseEntity<BaseApiResponseDTO<?>> ValidationErrorResponse(String msg, Map<String, List<String>> errors) {
//        ApiErrorResponseDTO errorResponseDTO = new ApiErrorResponseDTO(msg, errors);
//        return new ResponseEntity<>(errorResponseDTO, HttpStatus.OK);
//    }

    // Method 2: Accepts a single field name and error message, builds a standard error response
    public static ResponseEntity<BaseApiResponseDTO<?>> ValidationErrorResponse(String msg, Map<String, String> errors) {
        // Convert to ApiErrorResponseDTO however you prefer
        Map<String, List<String>> converted = errors.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue())));

        ApiErrorResponseDTO errorResponseDTO = new ApiErrorResponseDTO(msg, converted);
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.OK);
    }


}
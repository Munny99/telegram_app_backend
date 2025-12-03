package com.munni.telegram_app_backend.util.response;


import com.munni.telegram_app_backend.util.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApplicationExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseApiResponseDTO<?> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String msg = error.getDefaultMessage();
            errorMap.put(error.getField(), List.of(msg == null ? "Unknown error" : msg));
        });
        Map<String, String> simpleErrorMap = errorMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

        return ResponseUtils.ValidationErrorResponse("Validation failed", simpleErrorMap).getBody();

    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "Request body is missing");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

/*    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<Map<String, Object>> handleClassCastException(ClassCastException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("massage", "Authentication token is required " + ex.getMessage() );
        body.put("success", false);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }*/
}
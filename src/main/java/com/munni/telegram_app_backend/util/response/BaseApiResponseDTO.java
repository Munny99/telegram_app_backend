/**
 * Author      : monaum hossain
 * Created on  : 5/21/2025 at 10:54 AM
 */

package com.munni.telegram_app_backend.util.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseApiResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;

    public BaseApiResponseDTO(T data) {
        this.data = data;
    }

    public BaseApiResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public BaseApiResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }


}


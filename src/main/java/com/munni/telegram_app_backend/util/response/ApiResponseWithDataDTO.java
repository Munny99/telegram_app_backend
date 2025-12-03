package com.munni.telegram_app_backend.util.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiResponseWithDataDTO<T> extends BaseApiResponseDTO<T>{

    private T data;
    public ApiResponseWithDataDTO(boolean success, String message, T data) {
        super(success, message);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
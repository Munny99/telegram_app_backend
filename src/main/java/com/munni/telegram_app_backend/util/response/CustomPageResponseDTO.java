package com.munni.telegram_app_backend.util.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class CustomPageResponseDTO<T> {
    private int current_page;
    private List<T> data;
    private int from;
    private int last_page;
    private int per_page;
    private int to;
    private long total;

}
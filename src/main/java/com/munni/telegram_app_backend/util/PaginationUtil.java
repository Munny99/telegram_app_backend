package com.munni.telegram_app_backend.util;


import com.munni.telegram_app_backend.util.response.CustomPageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class PaginationUtil {

    public static <T> CustomPageResponseDTO<T> buildPageResponse(Page<T> page, Pageable pageable) {
        int currentPage = pageable.getPageNumber() + 1;
        int lastPage = page.getTotalPages();
        int from = (int) page.getPageable().getOffset() + 1;
        int to = from + page.getNumberOfElements() - 1;

        CustomPageResponseDTO<T> data = new CustomPageResponseDTO<>();
        data.setCurrent_page(currentPage);
        data.setData(page.getContent());
        data.setFrom(from);
        data.setLast_page(lastPage);
        data.setPer_page(pageable.getPageSize());
        data.setTo(to);
        data.setTotal(page.getTotalElements());

        return data;


    }


}
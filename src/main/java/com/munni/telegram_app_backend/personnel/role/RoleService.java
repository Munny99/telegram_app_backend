package com.munni.telegram_app_backend.personnel.role;

import com.munni.telegram_app_backend.util.PaginationUtil;
import com.munni.telegram_app_backend.util.ResponseUtils;
import com.munni.telegram_app_backend.util.response.BaseApiResponseDTO;
import com.munni.telegram_app_backend.util.response.CustomPageResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 23-Nov-25 10:08 PM
 */

@Service
public class RoleService {

    @Autowired RoleRepo repo;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(Pageable pageable) {
        Page<Role> roles = repo.findAllExcept(pageable);


        CustomPageResponseDTO<Role> paginatedResponse = PaginationUtil.buildPageResponse(roles, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }
}

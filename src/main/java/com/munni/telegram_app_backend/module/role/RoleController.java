package com.munni.telegram_app_backend.module.role;


import com.munni.telegram_app_backend.util.response.BaseApiResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 23-Nov-25 9:33 PM
 */

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired RoleService service;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.getAll(pageable);
    }
}

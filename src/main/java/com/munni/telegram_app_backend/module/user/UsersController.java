//package com.munni.telegram_app_backend.module.user;
//
//import com.munni.telegram_app_backend.util.response.BaseApiResponseDTO;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//@RestController
//@RequestMapping("/api/users")
//public class UsersController {
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping
//    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
//            @RequestParam(required = false) String search,
//            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
//        return userService.getAll(search, pageable);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<BaseApiResponseDTO<?>> getById(@PathVariable Long id) {
//        return userService.getById(id);
//    }
//
//    @PostMapping
//    public ResponseEntity<BaseApiResponseDTO<?>> create(
//            @Valid @RequestBody UserReqDTO dto) {
//        return userService.create(dto);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<BaseApiResponseDTO<?>> update(
//            @PathVariable Long id,
//            @Valid @RequestBody UserReqDTO dto) {
//        return userService.update(id, dto);
//    }
//
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<BaseApiResponseDTO<?>> updateStatus(
//            @PathVariable Long id,
//            @RequestParam Boolean isActive) {
//        return userService.updateStatus(id, isActive);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<BaseApiResponseDTO<?>> delete(@PathVariable Long id) {
//        return userService.delete(id);
//    }
//
//}

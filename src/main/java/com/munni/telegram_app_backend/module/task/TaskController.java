package com.munni.telegram_app_backend.module.task;

import com.munni.telegram_app_backend.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
public class TaskController {
    @Autowired TaskService service;

//    @GetMapping
//    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ){
//        Pageable pageable = PageRequest.of(page, size, Sort.by("sqn").ascending());
//
//        return service.getAll(pageable);
//    }
//
//    @PostMapping
//    public ResponseEntity<BaseApiResponseDTO<?>> create(
//            @Valid @RequestBody TaskReqDto req
//    ){
//        return service.create(req);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<BaseApiResponseDTO<?>> getById(
//            @PathVariable Long id
//    ){
//        return service.getById(id);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<BaseApiResponseDTO<?>> update(
//            @PathVariable Long id,
//            @Valid @RequestBody TaskReqDto req
//    ){
//        return service.update(id, req);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<BaseApiResponseDTO<?>> delete(
//            @PathVariable Long id
//    ){
//        return service.delete(id);
//    }
//
//


}

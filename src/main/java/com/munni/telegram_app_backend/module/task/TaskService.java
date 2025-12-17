package com.munni.telegram_app_backend.module.task;

import com.munni.telegram_app_backend.exception.CustomException;
import com.munni.telegram_app_backend.util.PaginationUtil;
import com.munni.telegram_app_backend.util.ResponseUtils;
import com.munni.telegram_app_backend.util.response.BaseApiResponseDTO;
import com.munni.telegram_app_backend.util.response.CustomPageResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired private TaskMapper taskmapper;
    @Autowired private TaskRepository taskrepo;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>>create(TaskReqDto dto ) {
        Task entity = taskmapper.toEntity(dto);
        entity = taskrepo.save(entity);

        return ResponseUtils.SuccessResponseWithData(taskmapper.toDTO(entity));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {
        Task task = taskrepo.findById(id).orElseThrow(()-> new CustomException("Task not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(taskmapper.toDTO(task));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(Pageable pageable){

        Page<TaskResDto> page = taskrepo.findAll(pageable).map(taskmapper::toDTO);
       CustomPageResponseDTO<TaskResDto> paginatedResponse = PaginationUtil.buildPageResponse(page, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }


    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, TaskReqDto dto) throws CustomException {
        Task task = taskrepo.findById(id).orElseThrow(() -> new CustomException("Task not found", HttpStatus.NOT_FOUND));

        taskmapper.toEntityUpdate(dto, task);
        taskrepo.save(task);

        return ResponseUtils.SuccessResponseWithData(taskmapper.toDTO(task));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) throws CustomException {
        Task task = taskrepo.findById(id).orElseThrow(() -> new CustomException("Task not found", HttpStatus.NOT_FOUND));

        taskrepo.delete(task);

        return ResponseUtils.SuccessResponse("Task has been deleted successfully", HttpStatus.OK);
    }


}

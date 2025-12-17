package com.munni.telegram_app_backend.withdrawal;

import com.munni.telegram_app_backend.exception.CustomException;
import com.munni.telegram_app_backend.task.*;
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
public class WithdrawalService {

    @Autowired private WithdrawalMapper withdrawalMapper;
    @Autowired private WithdrawalRepo withdrawalRepo;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(WithdrawalReqDto dto ) {
        Withdrawal entity = withdrawalMapper.toEntity(dto);
        entity = withdrawalRepo.save(entity);

        return ResponseUtils.SuccessResponseWithData(withdrawalMapper.toDTO(entity));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {
        Withdrawal withdrawal = withdrawalRepo.findById(id).orElseThrow(()-> new CustomException("Withdrawal not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(withdrawalMapper.toDTO(withdrawal));


    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(Pageable pageable){

        Page<WithdrawalResDto> page = withdrawalRepo.findAll(pageable).map(withdrawalMapper::toDTO);
        CustomPageResponseDTO<WithdrawalResDto> paginatedResponse = PaginationUtil.buildPageResponse(page, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, WithdrawalReqDto dto) throws CustomException {
        Withdrawal withdrawal = withdrawalRepo.findById(id).orElseThrow(() -> new CustomException("Withdrawal not found", HttpStatus.NOT_FOUND));

        withdrawalMapper.toEntityUpdate(dto, withdrawal);
        withdrawalRepo.save(withdrawal);

        return ResponseUtils.SuccessResponseWithData(withdrawalMapper.toDTO(withdrawal));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) throws CustomException {
        Withdrawal withdrawal = withdrawalRepo.findById(id).orElseThrow(() -> new CustomException("Withdrawal not found", HttpStatus.NOT_FOUND));

        withdrawalRepo.delete(withdrawal);

        return ResponseUtils.SuccessResponse("Withdrawal has been deleted successfully", HttpStatus.OK);
    }

}

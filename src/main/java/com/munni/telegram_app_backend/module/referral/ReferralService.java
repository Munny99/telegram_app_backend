package com.munni.telegram_app_backend.module.referral;

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
public class ReferralService {

    @Autowired private ReferralMapper referralMapper;
    @Autowired private ReferralRepo referralRepo;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(ReferralReqDto dto ) {
        Referral entity = referralMapper.toEntity(dto);
        entity = referralRepo.save(entity);

        return ResponseUtils.SuccessResponseWithData(referralMapper.toDTO(entity));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {
        Referral referral = referralRepo.findById(id).orElseThrow(()-> new CustomException("Referral not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(referralMapper.toDTO(referral));


    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(Pageable pageable){

        Page<ReferralResDto> page = referralRepo.findAll(pageable).map(referralMapper::toDTO);
        CustomPageResponseDTO<ReferralResDto> paginatedResponse = PaginationUtil.buildPageResponse(page, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, ReferralReqDto dto) throws CustomException {
        Referral referral = referralRepo.findById(id).orElseThrow(() -> new CustomException("Referral not found", HttpStatus.NOT_FOUND));

        referralMapper.toEntityUpdate(dto, referral);
        referralRepo.save(referral);

        return ResponseUtils.SuccessResponseWithData(referralMapper.toDTO(referral));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) throws CustomException {
        Referral referral = referralRepo.findById(id).orElseThrow(() -> new CustomException("Referral not found", HttpStatus.NOT_FOUND));

        referralRepo.delete(referral);

        return ResponseUtils.SuccessResponse("Referral has been deleted successfully", HttpStatus.OK);
    }





}

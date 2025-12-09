package com.munni.telegram_app_backend.referral;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReferralMapper {

    Referral toEntity(ReferralReqDto dto);

    ReferralResDto toDTO(Referral entity);
    void toEntity(ReferralReqDto dto, @MappingTarget Referral referral);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(ReferralReqDto dto, @MappingTarget Referral entity);
}

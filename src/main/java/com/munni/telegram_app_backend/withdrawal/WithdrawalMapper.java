package com.munni.telegram_app_backend.withdrawal;

import com.munni.telegram_app_backend.task.Task;
import com.munni.telegram_app_backend.task.TaskReqDto;
import com.munni.telegram_app_backend.task.TaskResDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface WithdrawalMapper {

    Withdrawal toEntity(WithdrawalReqDto dto);

    WithdrawalResDto toDTO(Withdrawal entity);
    void toEntity(WithdrawalReqDto dto, @MappingTarget Withdrawal withdrawal);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(WithdrawalReqDto dto, @MappingTarget Withdrawal entity);


}

package com.munni.telegram_app_backend.module.task;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(TaskReqDto dto);

    TaskResDto toDTO(Task entity);
    void toEntity(TaskReqDto dto, @MappingTarget Task task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(TaskReqDto dto, @MappingTarget Task entity);
}

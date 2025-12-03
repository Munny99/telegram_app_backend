package com.munni.telegram_app_backend.personnel.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 */

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserReqDTO dto);


    UserResDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntityFromDto(UserReqDTO dto, @MappingTarget User user);
}
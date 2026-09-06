package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserResponseMapper {
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "role", source = "role.name")
    UserResponseDto toResponse(User user);
}

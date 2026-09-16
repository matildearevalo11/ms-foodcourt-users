package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.UserRoleResponseDto;
import com.pragma.powerup.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserRoleResponseMapper {
    @Mapping(target = "role", source = "role.name")
    UserRoleResponseDto toResponse(User user);
}

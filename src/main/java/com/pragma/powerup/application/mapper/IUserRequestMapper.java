package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role.id", source = "roleId")
    @Mapping(target = "role.name", ignore = true)
    User toUser(UserRequestDto request);
}

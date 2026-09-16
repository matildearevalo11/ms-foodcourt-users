package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.CustomerRequestDto;
import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.OwnerRequestDto;
import com.pragma.powerup.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "restaurantId", ignore = true)
    User toUser(OwnerRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(EmployeeRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "restaurantId", ignore = true)
    User toUser(CustomerRequestDto request);
}

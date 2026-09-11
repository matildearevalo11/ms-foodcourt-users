package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.OwnerRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.dto.response.UserRoleResponseDto;

public interface IUserHandler {
    UserResponseDto createOwner(OwnerRequestDto request);

    UserResponseDto createEmployee(EmployeeRequestDto request);

    UserRoleResponseDto getUserRole(Long userId);
}

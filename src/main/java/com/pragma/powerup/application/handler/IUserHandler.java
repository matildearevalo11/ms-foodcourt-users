package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.CustomerRequestDto;
import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.OwnerRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.dto.response.UserRoleResponseDto;
import com.pragma.powerup.application.dto.response.CustomerContactResponseDto;

public interface IUserHandler {
    UserResponseDto createOwner(OwnerRequestDto request);

    UserResponseDto createEmployee(EmployeeRequestDto request);

    UserResponseDto createCustomer(CustomerRequestDto request);

    UserRoleResponseDto getUserRole(Long userId);

    CustomerContactResponseDto getCustomerContact(Long userId);
}

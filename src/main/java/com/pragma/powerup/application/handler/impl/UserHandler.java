package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.OwnerRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.dto.response.UserRoleResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.application.mapper.IUserResponseMapper;
import com.pragma.powerup.application.mapper.IUserRoleResponseMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserHandler implements IUserHandler {
    private final IUserServicePort servicePort;
    private final IUserRequestMapper requestMapper;
    private final IUserResponseMapper responseMapper;
    private final IUserRoleResponseMapper roleResponseMapper;

    @Override
    public UserResponseDto createOwner(OwnerRequestDto request) {
        return responseMapper.toResponse(servicePort.createOwner(requestMapper.toUser(request)));
    }

    @Override
    public UserResponseDto createEmployee(EmployeeRequestDto request) {
        return responseMapper.toResponse(servicePort.createEmployee(requestMapper.toUser(request), request.roleId()));
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleResponseDto getUserRole(Long userId) {
        return roleResponseMapper.toResponse(servicePort.getUserById(userId));
    }
}

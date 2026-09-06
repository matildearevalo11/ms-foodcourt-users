package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.application.mapper.IUserResponseMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserHandler implements IUserHandler {
    private final IUserServicePort servicePort;
    private final IUserRequestMapper requestMapper;
    private final IUserResponseMapper responseMapper;

    public UserHandler(IUserServicePort servicePort, IUserRequestMapper requestMapper,
                       IUserResponseMapper responseMapper) {
        this.servicePort = servicePort;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        return responseMapper.toResponse(servicePort.createUser(requestMapper.toUser(request)));
    }
}

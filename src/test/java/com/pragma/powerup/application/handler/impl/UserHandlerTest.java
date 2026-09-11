package com.pragma.powerup.application.handler.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pragma.powerup.application.dto.request.OwnerRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.dto.response.UserRoleResponseDto;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.application.mapper.IUserResponseMapper;
import com.pragma.powerup.application.mapper.IUserRoleResponseMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {
    @Mock IUserServicePort servicePort;
    @Mock IUserRequestMapper requestMapper;
    @Mock IUserResponseMapper responseMapper;
    @Mock IUserRoleResponseMapper roleResponseMapper;

    @Test
    void delegatesMappingAndCreationToTheApplicationPorts() {
        OwnerRequestDto request = new OwnerRequestDto("Ana", "Rojas", "123", "+573001234567",
                LocalDate.of(1990, 1, 1), "ana@example.com", "secret");
        User mapped = new User();
        User saved = new User();
        UserResponseDto expected = new UserResponseDto(1L, "Ana", "Rojas", "ana@example.com", 2L, "OWNER");
        when(requestMapper.toUser(request)).thenReturn(mapped);
        when(servicePort.createOwner(mapped)).thenReturn(saved);
        when(responseMapper.toResponse(saved)).thenReturn(expected);

        UserHandler handler = new UserHandler(servicePort, requestMapper, responseMapper, roleResponseMapper);

        assertThat(handler.createOwner(request)).isEqualTo(expected);
        verify(servicePort).createOwner(mapped);
    }

    @Test
    void returnsOnlyTheRequestedUsersRole() {
        User user = new User();
        UserRoleResponseDto expected = new UserRoleResponseDto(1L, "OWNER");
        when(servicePort.getUserById(1L)).thenReturn(user);
        when(roleResponseMapper.toResponse(user)).thenReturn(expected);
        UserHandler handler = new UserHandler(servicePort, requestMapper, responseMapper, roleResponseMapper);

        assertThat(handler.getUserRole(1L)).isEqualTo(expected);
    }
}

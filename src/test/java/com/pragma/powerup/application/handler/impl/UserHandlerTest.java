package com.pragma.powerup.application.handler.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.application.mapper.IUserResponseMapper;
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

    @Test
    void delegatesMappingAndCreationToTheApplicationPorts() {
        UserRequestDto request = new UserRequestDto("Ana", "Rojas", "123", "+573001234567",
                LocalDate.of(1990, 1, 1), "ana@example.com", "secret", 2L);
        User mapped = new User();
        User saved = new User();
        UserResponseDto expected = new UserResponseDto(1L, "Ana", "Rojas", "ana@example.com", 2L, "OWNER");
        when(requestMapper.toUser(request)).thenReturn(mapped);
        when(servicePort.createUser(mapped)).thenReturn(saved);
        when(responseMapper.toResponse(saved)).thenReturn(expected);

        UserHandler handler = new UserHandler(servicePort, requestMapper, responseMapper);

        assertThat(handler.createUser(request)).isEqualTo(expected);
        verify(servicePort).createUser(mapped);
    }
}

package com.pragma.powerup.application.handler.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.domain.exception.AuthenticationException;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.ITokenProviderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {
    @Mock IUserPersistencePort persistencePort;
    @Mock IPasswordEncoderPort passwordEncoderPort;
    @Mock ITokenProviderPort tokenProviderPort;

    @Test
    void authenticatesNormalizedEmailAndReturnsBearerToken() {
        User user = new User();
        user.setPassword("hash");
        when(persistencePort.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches("secret", "hash")).thenReturn(true);
        when(tokenProviderPort.generate(user)).thenReturn("jwt");
        AuthHandler handler = new AuthHandler(persistencePort, passwordEncoderPort, tokenProviderPort);

        var response = handler.login(new LoginRequestDto(" OWNER@EXAMPLE.COM ", "secret"));

        assertThat(response.token()).isEqualTo("jwt");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void rejectsUnknownUserOrIncorrectPasswordWithSameError() {
        AuthHandler handler = new AuthHandler(persistencePort, passwordEncoderPort, tokenProviderPort);
        LoginRequestDto request = new LoginRequestDto("owner@example.com", "wrong");
        when(persistencePort.findByEmail(request.email())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> handler.login(request)).isInstanceOf(AuthenticationException.class);

        User user = new User();
        user.setPassword("hash");
        when(persistencePort.findByEmail(request.email())).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> handler.login(request)).isInstanceOf(AuthenticationException.class);
    }
}

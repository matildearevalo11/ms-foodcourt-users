package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.LoginResponseDto;
import com.pragma.powerup.application.handler.IAuthHandler;
import com.pragma.powerup.domain.exception.AuthenticationException;
import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.ITokenProviderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthHandler implements IAuthHandler {
    private static final String TOKEN_TYPE = "Bearer";
    private final IUserPersistencePort userPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final ITokenProviderPort tokenProviderPort;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        User user = userPersistencePort.findByEmail(email).orElseThrow(this::invalidCredentials);
        if (!passwordEncoderPort.matches(request.password(), user.getPassword())) {
            throw invalidCredentials();
        }
        return new LoginResponseDto(tokenProviderPort.generate(user), TOKEN_TYPE);
    }

    private AuthenticationException invalidCredentials() {
        return new AuthenticationException(ExceptionMessages.INVALID_CREDENTIALS.getMessage());
    }
}

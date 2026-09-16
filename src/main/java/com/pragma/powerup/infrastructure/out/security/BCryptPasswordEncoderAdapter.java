package com.pragma.powerup.infrastructure.out.security;

import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BCryptPasswordEncoderAdapter implements IPasswordEncoderPort {
    private final PasswordEncoder delegate;
    @Override public String encode(String rawPassword) { return delegate.encode(rawPassword); }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return delegate.matches(rawPassword, encodedPassword);
    }
}

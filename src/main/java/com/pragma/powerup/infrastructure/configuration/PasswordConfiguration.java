package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.infrastructure.out.security.BCryptPasswordEncoderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordConfiguration {
    @Bean
    IPasswordEncoderPort passwordEncoderPort() {
        return new BCryptPasswordEncoderAdapter(new BCryptPasswordEncoder());
    }
}

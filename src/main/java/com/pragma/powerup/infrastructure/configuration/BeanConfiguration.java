package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.usecase.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean IUserServicePort userServicePort(IUserPersistencePort persistencePort,
                                           IPasswordEncoderPort passwordEncoderPort) {
        return new UserUseCase(persistencePort, passwordEncoderPort);
    }
}

package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.User;
import java.util.Optional;

public interface IUserPersistencePort {
    User save(User user);
    boolean existsByEmail(String email);
    boolean existsByIdentityDocument(String identityDocument);
    Optional<User> findById(Long userId);
    Optional<User> findByEmail(String email);
}

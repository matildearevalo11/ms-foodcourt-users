package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserJpaAdapter implements IUserPersistencePort {
    private final IUserRepository repository;
    private final IUserEntityMapper mapper;
    public UserJpaAdapter(IUserRepository repository, IUserEntityMapper mapper) {
        this.repository = repository; this.mapper = mapper;
    }
    @Override public User save(User user) { return mapper.toDomain(repository.save(mapper.toEntity(user))); }
    @Override public boolean existsByEmail(String email) { return repository.existsByEmailIgnoreCase(email); }
    @Override public boolean existsByIdentityDocument(String document) { return repository.existsByIdentityDocument(document); }
}

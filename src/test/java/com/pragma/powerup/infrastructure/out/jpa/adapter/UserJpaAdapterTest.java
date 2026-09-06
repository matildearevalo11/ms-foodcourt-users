package com.pragma.powerup.infrastructure.out.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserJpaAdapterTest {
    @Mock IUserRepository repository;
    @Mock IUserEntityMapper mapper;

    @Test
    void delegatesPersistenceAndExistenceQueries() {
        User user = new User();
        User saved = new User();
        UserEntity entity = new UserEntity();
        when(mapper.toEntity(user)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(saved);
        when(repository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(true);
        when(repository.existsByIdentityDocument("123")).thenReturn(true);
        UserJpaAdapter adapter = new UserJpaAdapter(repository, mapper);

        assertThat(adapter.save(user)).isSameAs(saved);
        assertThat(adapter.existsByEmail("ana@example.com")).isTrue();
        assertThat(adapter.existsByIdentityDocument("123")).isTrue();
    }
}

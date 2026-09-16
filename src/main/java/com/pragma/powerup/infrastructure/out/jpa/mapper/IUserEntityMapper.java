package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserEntityMapper {
    UserEntity toEntity(User user);
    User toDomain(UserEntity entity);
}

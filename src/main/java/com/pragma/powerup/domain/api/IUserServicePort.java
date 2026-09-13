package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.User;

public interface IUserServicePort {
    User createOwner(User user);

    User createEmployee(User user, Long roleId);

    User createCustomer(User user, Long roleId);

    User getUserById(Long userId);
}

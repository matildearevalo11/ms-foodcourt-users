package com.pragma.powerup.domain.enums;

import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.exception.ValidationException;

public enum RoleEnum {
    ADMIN(1L), OWNER(2L), EMPLOYEE(3L), CUSTOMER(4L);

    private final Long id;
    RoleEnum(Long id) { this.id = id; }
    public Long getId() { return id; }

    public static RoleEnum fromId(Long id) {
        for (RoleEnum role : values()) {
            if (role.id.equals(id)) return role;
        }
        throw new ValidationException(ExceptionMessages.INVALID_ROLE.getMessage());
    }
}

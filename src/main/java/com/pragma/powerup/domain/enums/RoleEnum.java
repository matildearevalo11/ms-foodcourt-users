package com.pragma.powerup.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleEnum {
    ADMIN(1L), OWNER(2L), EMPLOYEE(3L), CUSTOMER(4L);

    private final Long id;
}

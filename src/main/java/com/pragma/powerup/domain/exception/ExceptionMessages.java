package com.pragma.powerup.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessages {
    USER_NOT_ADULT("User must be at least 18 years old"),
    EMAIL_ALREADY_EXISTS("A user with this email already exists"),
    IDENTITY_ALREADY_EXISTS("A user with this identity document already exists"),
    USER_NOT_FOUND("The requested user does not exist"),
    INVALID_CREDENTIALS("Invalid email or password"),
    AUTHENTICATED_USER_NOT_FOUND("Authenticated user not found"),
    ACCESS_DENIED("The authenticated user does not have permission to perform this action"),
    RESTAURANT_NOT_FOUND("The requested restaurant does not exist"),
    RESTAURANT_OWNER_REQUIRED("Only the restaurant owner can create its employees"),
    FOODCOURT_SERVICE_UNAVAILABLE("Food court service is unavailable"),
    INVALID_EMPLOYEE_ROLE("Role id must correspond to EMPLOYEE"),
    INVALID_CUSTOMER_ROLE("Role id must correspond to CUSTOMER");

    private final String message;
}

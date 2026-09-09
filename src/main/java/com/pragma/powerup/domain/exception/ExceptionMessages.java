package com.pragma.powerup.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessages {
    USER_NOT_ADULT("User must be at least 18 years old"),
    EMAIL_ALREADY_EXISTS("A user with this email already exists"),
    IDENTITY_ALREADY_EXISTS("A user with this identity document already exists"),
    ROLE_NOT_ALLOWED("Only OWNER users can be created in the current user story"),
    INVALID_ROLE("The requested role does not exist"),
    USER_NOT_FOUND("The requested user does not exist"),
    INVALID_CREDENTIALS("Invalid email or password"),
    AUTHENTICATED_USER_NOT_FOUND("Authenticated user not found"),
    ACCESS_DENIED("The authenticated user does not have permission to perform this action");

    private final String message;
}

package com.pragma.powerup.domain.usecase;

import static com.pragma.powerup.domain.exception.ExceptionMessages.EMAIL_ALREADY_EXISTS;
import static com.pragma.powerup.domain.exception.ExceptionMessages.IDENTITY_ALREADY_EXISTS;
import static com.pragma.powerup.domain.exception.ExceptionMessages.USER_NOT_ADULT;
import static com.pragma.powerup.domain.exception.ExceptionMessages.ROLE_NOT_ALLOWED;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.enums.RoleEnum;
import com.pragma.powerup.domain.exception.ValidationException;
import com.pragma.powerup.domain.model.Role;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import java.time.LocalDate;

public class UserUseCase implements IUserServicePort {
    private static final int MINIMUM_AGE = 18;
    private final IUserPersistencePort persistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;

    public UserUseCase(IUserPersistencePort persistencePort, IPasswordEncoderPort passwordEncoderPort) {
        this.persistencePort = persistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public User createUser(User user) {
        RoleEnum targetRole = RoleEnum.fromId(user.getRole().getId());
        validateTargetRole(targetRole);
        normalize(user);
        validateAdult(user.getBirthDate());
        validateUniqueness(user);
        user.setRole(new Role(targetRole));
        user.setPassword(passwordEncoderPort.encode(user.getPassword()));
        return persistencePort.save(user);
    }

    private void validateTargetRole(RoleEnum targetRole) {
        if (targetRole != RoleEnum.OWNER) {
            throw new ValidationException(ROLE_NOT_ALLOWED.getMessage());
        }
    }

    private void normalize(User user) {
        user.setName(user.getName().trim());
        user.setLastName(user.getLastName().trim());
        user.setIdentityDocument(user.getIdentityDocument().trim());
        user.setCellphone(user.getCellphone().trim());
        user.setEmail(user.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
    }

    private void validateAdult(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        if (birthDate == null || birthDate.isAfter(today.minusYears(MINIMUM_AGE))) {
            throw new ValidationException(USER_NOT_ADULT.getMessage());
        }
    }

    private void validateUniqueness(User user) {
        if (persistencePort.existsByEmail(user.getEmail())) {
            throw new ValidationException(EMAIL_ALREADY_EXISTS.getMessage());
        }
        if (persistencePort.existsByIdentityDocument(user.getIdentityDocument())) {
            throw new ValidationException(IDENTITY_ALREADY_EXISTS.getMessage());
        }
    }
}

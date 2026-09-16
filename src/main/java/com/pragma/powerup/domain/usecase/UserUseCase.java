package com.pragma.powerup.domain.usecase;

import static com.pragma.powerup.domain.exception.ExceptionMessages.EMAIL_ALREADY_EXISTS;
import static com.pragma.powerup.domain.exception.ExceptionMessages.IDENTITY_ALREADY_EXISTS;
import static com.pragma.powerup.domain.exception.ExceptionMessages.USER_NOT_ADULT;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.enums.RoleEnum;
import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.exception.NotFoundException;
import com.pragma.powerup.domain.exception.ValidationException;
import com.pragma.powerup.domain.model.Role;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IRestaurantValidationPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import java.time.LocalDate;

public class UserUseCase implements IUserServicePort {
    private static final int MINIMUM_AGE = 18;
    private final IUserPersistencePort persistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final IRestaurantValidationPort restaurantValidationPort;

    public UserUseCase(IUserPersistencePort persistencePort, IPasswordEncoderPort passwordEncoderPort,
                       IRestaurantValidationPort restaurantValidationPort) {
        this.persistencePort = persistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.restaurantValidationPort = restaurantValidationPort;
    }

    @Override
    public User createOwner(User user) {
        normalize(user);
        validateAdult(user.getBirthDate());
        validateUniqueness(user);
        user.setRole(new Role(RoleEnum.OWNER));
        user.setPassword(passwordEncoderPort.encode(user.getPassword()));
        return persistencePort.save(user);
    }

    @Override
    public User createEmployee(User user, Long roleId) {
        validateRole(roleId, RoleEnum.EMPLOYEE, ExceptionMessages.INVALID_EMPLOYEE_ROLE);
        restaurantValidationPort.validateOwnership(user.getRestaurantId());
        normalize(user);
        validateUniqueness(user);
        user.setRole(new Role(RoleEnum.EMPLOYEE));
        user.setPassword(passwordEncoderPort.encode(user.getPassword()));
        return persistencePort.save(user);
    }

    @Override
    public User createCustomer(User user, Long roleId) {
        validateRole(roleId, RoleEnum.CUSTOMER, ExceptionMessages.INVALID_CUSTOMER_ROLE);
        normalize(user);
        validateUniqueness(user);
        user.setRole(new Role(RoleEnum.CUSTOMER));
        user.setPassword(passwordEncoderPort.encode(user.getPassword()));
        return persistencePort.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return persistencePort.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.USER_NOT_FOUND.getMessage()));
    }

    @Override
    public User getCustomerById(Long userId) {
        User user = getUserById(userId);
        if (user.getRole() == null || !RoleEnum.CUSTOMER.name().equals(user.getRole().getName())) {
            throw new ValidationException(ExceptionMessages.CUSTOMER_ROLE_REQUIRED.getMessage());
        }
        return user;
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

    private void validateRole(Long roleId, RoleEnum expectedRole, ExceptionMessages message) {
        if (!expectedRole.getId().equals(roleId)) {
            throw new ValidationException(message.getMessage());
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

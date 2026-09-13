package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import com.pragma.powerup.domain.enums.RoleEnum;
import com.pragma.powerup.domain.exception.ValidationException;
import com.pragma.powerup.domain.exception.NotFoundException;
import com.pragma.powerup.domain.model.Role;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantValidationPort;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {
    @Mock IUserPersistencePort persistence;
    @Mock IPasswordEncoderPort encoder;
    @Mock IRestaurantValidationPort restaurantValidationPort;
    private UserUseCase useCase;

    @BeforeEach void setUp() {
        useCase = new UserUseCase(persistence, encoder, restaurantValidationPort);
    }

    @Test void createsOwnerWithNormalizedDataAndEncryptedPassword() {
        User user = validUser(LocalDate.of(2000, 1, 1));
        user.setEmail("  OWNER@Example.COM ");
        when(encoder.encode("secret")).thenReturn("bcrypt-hash");
        when(persistence.save(user)).thenReturn(user);

        User result = useCase.createOwner(user);

        assertThat(result.getRole().getName()).isEqualTo("OWNER");
        assertThat(result.getEmail()).isEqualTo("owner@example.com");
        assertThat(result.getPassword()).isEqualTo("bcrypt-hash");
        verify(persistence).save(user);
    }

    @Test void ownerMustBeAtLeastEighteen() {
        assertThatThrownBy(() -> useCase.createOwner(validUser(LocalDate.now().minusYears(18).plusDays(1))))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void createsEmployeeForOwnedRestaurantWithEncryptedPassword() {
        User employee = validUser(null);
        employee.setRestaurantId(5L);
        employee.setEmail(" EMPLOYEE@Example.COM ");
        when(encoder.encode("secret")).thenReturn("bcrypt-hash");
        when(persistence.save(employee)).thenAnswer(invocation -> {
            employee.setId(9L);
            return employee;
        });

        User result = useCase.createEmployee(employee, RoleEnum.EMPLOYEE.getId());

        assertThat(result.getRole().getName()).isEqualTo("EMPLOYEE");
        assertThat(result.getEmail()).isEqualTo("employee@example.com");
        assertThat(result.getPassword()).isEqualTo("bcrypt-hash");
        verify(restaurantValidationPort).validateOwnership(5L);
    }

    @Test
    void employeeRoleMustBeRequested() {
        User employee = validUser(null);
        employee.setRestaurantId(5L);

        assertThatThrownBy(() -> useCase.createEmployee(employee, RoleEnum.OWNER.getId()))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(restaurantValidationPort, encoder, persistence);
    }

    @Test
    void createsCustomerWithNormalizedDataAndEncryptedPassword() {
        User customer = validUser(null);
        customer.setEmail(" CUSTOMER@Example.COM ");
        when(encoder.encode("secret")).thenReturn("bcrypt-hash");
        when(persistence.save(customer)).thenReturn(customer);

        User result = useCase.createCustomer(customer, RoleEnum.CUSTOMER.getId());

        assertThat(result.getRole().getName()).isEqualTo("CUSTOMER");
        assertThat(result.getEmail()).isEqualTo("customer@example.com");
        assertThat(result.getPassword()).isEqualTo("bcrypt-hash");
        assertThat(result.getRestaurantId()).isNull();
        verify(persistence).save(customer);
    }

    @Test
    void customerRoleMustBeRequested() {
        User customer = validUser(null);

        assertThatThrownBy(() -> useCase.createCustomer(customer, RoleEnum.OWNER.getId()))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(restaurantValidationPort, encoder, persistence);
    }

    @Test void duplicatedEmailIsInvalid() {
        User user = validUser(LocalDate.of(2000, 1, 1));
        when(persistence.existsByEmail(user.getEmail())).thenReturn(true);
        assertThatThrownBy(() -> useCase.createOwner(user)).isInstanceOf(ValidationException.class);
        verify(encoder, never()).encode(anyString());
    }

    @Test void duplicatedDocumentIsInvalid() {
        User user = validUser(LocalDate.of(2000, 1, 1));
        when(persistence.existsByIdentityDocument(user.getIdentityDocument())).thenReturn(true);
        assertThatThrownBy(() -> useCase.createOwner(user)).isInstanceOf(ValidationException.class);
    }

    @Test void getsAnExistingUser() {
        User user = validUser(LocalDate.of(2000, 1, 1));
        when(persistence.findById(1L)).thenReturn(Optional.of(user));

        assertThat(useCase.getUserById(1L)).isSameAs(user);
    }

    @Test void rejectsAnUnknownUser() {
        when(persistence.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getUserById(99L)).isInstanceOf(NotFoundException.class);
    }

    private User validUser(LocalDate birthDate) {
        User user = new User(); user.setName(" Ana "); user.setLastName(" Admin ");
        user.setIdentityDocument("123456"); user.setCellphone("+573001234567");
        user.setBirthDate(birthDate); user.setEmail("owner@example.com"); user.setPassword("secret");
        return user;
    }
}

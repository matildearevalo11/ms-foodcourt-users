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
    private UserUseCase useCase;

    @BeforeEach void setUp() { useCase = new UserUseCase(persistence, encoder); }

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

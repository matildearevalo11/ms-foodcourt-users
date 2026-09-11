package com.pragma.powerup.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record OwnerRequestDto(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Identity document is required")
        @Pattern(regexp = "\\d+", message = "Identity document must contain only numbers")
        String identityDocument,

        @NotBlank(message = "Cellphone is required")
        @Size(max = 13, message = "Cellphone must be at most 13 characters")
        @Pattern(regexp = "\\+?\\d+", message = "Invalid cellphone format")
        String cellphone,

        @NotNull(message = "Birth date is required")
        LocalDate birthDate,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}

package com.pragma.powerup.domain.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private String lastName;
    private String identityDocument;
    private String cellphone;
    private LocalDate birthDate;
    private String email;
    private String password;
    private Role role;
    private Long restaurantId;

}

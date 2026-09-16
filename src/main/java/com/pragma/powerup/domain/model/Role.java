package com.pragma.powerup.domain.model;

import com.pragma.powerup.domain.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private Long id;
    private String name;

    public Role(RoleEnum role) {
        this.id = role.getId();
        this.name = role.name();
    }
}

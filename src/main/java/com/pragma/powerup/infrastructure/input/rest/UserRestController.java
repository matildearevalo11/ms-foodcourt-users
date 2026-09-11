package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OwnerRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.dto.response.UserRoleResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.domain.enums.RoleEnum;
import com.pragma.powerup.infrastructure.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserRestController {
    private final IUserHandler handler;

    @PostMapping(value = "/owners", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequireRole(RoleEnum.ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultResponse<UserResponseDto> createOwner(@Valid @RequestBody OwnerRequestDto request) {
        return new DefaultResponse<>(handler.createOwner(request));
    }

    @GetMapping(value = "/{userId}/role", produces = MediaType.APPLICATION_JSON_VALUE)
    public DefaultResponse<UserRoleResponseDto> getUserRole(@PathVariable Long userId) {
        return new DefaultResponse<>(handler.getUserRole(userId));
    }
}

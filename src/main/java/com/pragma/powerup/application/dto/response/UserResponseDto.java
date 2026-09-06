package com.pragma.powerup.application.dto.response;

public record UserResponseDto(Long id, String name, String lastName, String email, Long roleId, String role) { }

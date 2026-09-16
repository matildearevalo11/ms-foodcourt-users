package com.pragma.powerup.domain.spi;

public interface IRestaurantValidationPort {
    void validateOwnership(Long restaurantId);
}

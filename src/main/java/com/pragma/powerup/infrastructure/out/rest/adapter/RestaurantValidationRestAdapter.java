package com.pragma.powerup.infrastructure.out.rest.adapter;

import com.pragma.powerup.domain.exception.AuthenticationException;
import com.pragma.powerup.domain.exception.AuthorizationException;
import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.exception.ExternalServiceException;
import com.pragma.powerup.domain.exception.NotFoundException;
import com.pragma.powerup.domain.spi.IRestaurantValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class RestaurantValidationRestAdapter implements IRestaurantValidationPort {
    private final RestClient foodcourtRestClient;

    @Override
    public void validateOwnership(Long restaurantId) {
        try {
            foodcourtRestClient.get()
                    .uri("/restaurants/{restaurantId}/ownership", restaurantId)
                    .headers(headers -> headers.setBearerAuth(token()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NotFoundException(ExceptionMessages.RESTAURANT_NOT_FOUND.getMessage());
        } catch (HttpClientErrorException.Forbidden exception) {
            throw new AuthorizationException(ExceptionMessages.RESTAURANT_OWNER_REQUIRED.getMessage());
        } catch (RestClientException exception) {
            throw new ExternalServiceException(ExceptionMessages.FOODCOURT_SERVICE_UNAVAILABLE.getMessage());
        }
    }

    private String token() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AuthenticationException(ExceptionMessages.AUTHENTICATED_USER_NOT_FOUND.getMessage());
        }
        return jwt.getTokenValue();
    }
}

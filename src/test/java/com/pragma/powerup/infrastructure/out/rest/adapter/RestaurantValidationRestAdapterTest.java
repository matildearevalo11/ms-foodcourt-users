package com.pragma.powerup.infrastructure.out.rest.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withForbiddenRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import com.pragma.powerup.domain.exception.AuthorizationException;
import com.pragma.powerup.domain.exception.ExternalServiceException;
import com.pragma.powerup.domain.exception.NotFoundException;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class RestaurantValidationRestAdapterTest {
    private MockRestServiceServer server;
    private RestaurantValidationRestAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://foodcourt/api/v1");
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new RestaurantValidationRestAdapter(builder.build());
        Jwt jwt = new Jwt("jwt-token", Instant.now(), Instant.now().plusSeconds(300),
                Map.of("alg", "none"), Map.of("sub", "7", "role", "OWNER"));
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validatesOwnershipWithBearerToken() {
        server.expect(once(), requestTo("http://foodcourt/api/v1/restaurants/5/ownership"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer jwt-token"))
                .andRespond(withSuccess());

        adapter.validateOwnership(5L);

        server.verify();
    }

    @Test
    void reportsMissingRestaurant() {
        server.expect(requestTo("http://foodcourt/api/v1/restaurants/99/ownership"))
                .andRespond(withResourceNotFound());

        assertThatThrownBy(() -> adapter.validateOwnership(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void reportsRestaurantOwnedByAnotherUser() {
        server.expect(requestTo("http://foodcourt/api/v1/restaurants/5/ownership"))
                .andRespond(withForbiddenRequest());

        assertThatThrownBy(() -> adapter.validateOwnership(5L))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void reportsFoodcourtServiceFailure() {
        server.expect(requestTo("http://foodcourt/api/v1/restaurants/5/ownership"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> adapter.validateOwnership(5L))
                .isInstanceOf(ExternalServiceException.class);
    }
}

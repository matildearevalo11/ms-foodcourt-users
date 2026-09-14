package com.pragma.powerup.infrastructure.out.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.pragma.powerup.domain.model.Role;
import com.pragma.powerup.domain.model.User;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class JwtTokenAdapterTest {
    @Test
    void generatesSignedTokenWithIdentityAndRole() {
        String secret = "0123456789abcdef0123456789abcdef";
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JwtTokenAdapter adapter = new JwtTokenAdapter(new NimbusJwtEncoder(new ImmutableSecret<>(key)), Duration.ofHours(1));
        User user = new User();
        user.setId(7L);
        user.setEmail("employee@example.com");
        user.setRole(new Role(3L, "EMPLOYEE"));
        user.setRestaurantId(5L);

        var jwt = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build()
                .decode(adapter.generate(user));

        assertThat(jwt.getSubject()).isEqualTo("7");
        assertThat(jwt.getClaimAsString("role")).isEqualTo("EMPLOYEE");
        assertThat(((Number) jwt.getClaim("restaurantId")).longValue()).isEqualTo(5L);
        assertThat(jwt.getExpiresAt()).isAfter(jwt.getIssuedAt());
    }
}

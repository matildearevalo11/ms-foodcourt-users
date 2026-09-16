package com.pragma.powerup.infrastructure.out.security;

import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.ITokenProviderPort;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

@RequiredArgsConstructor
public class JwtTokenAdapter implements ITokenProviderPort {
    private final JwtEncoder encoder;
    private final Duration expiration;

    @Override
    public String generate(User user) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(expiration))
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().getName());
        if (user.getRestaurantId() != null) {
            claims.claim("restaurantId", user.getRestaurantId());
        }
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }
}

package com.gustavomp.devtrackerapi.services.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gustavomp.devtrackerapi.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    @Value("${api.security.jwt.secret}")
    private String secret;

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer("DevTracker API")
                .withSubject(user.getUsername())
                .withExpiresAt(generatesExpirationTime())
                .sign(algorithm);
    }

    private Instant generatesExpirationTime() {
        return Instant.now().plus(2, ChronoUnit.HOURS);
    }

    public String getSubject(String jwtToken) {
        try {
            var algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("DevTracker API")
                    .build()
                    .verify(jwtToken)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new BadCredentialsException("JWT token invalid or expired");
        }
    }

}

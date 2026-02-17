package com.kiranastore.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    /**
     * Creates the signing key used to sign and verify JWTs.
     *
     * @param secret signing secret from configuration
     * @return HMAC signing key
     */
    @Bean
    public SecretKey jwtSigningKey(@Value("${jwt.secret}") String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}

package com.kiranastore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMillis;

    /**
     * Creates a JWT service using the configured signing key and expiration.
     *
     * @param signingKey signing key bean
     * @param expirationMillis token lifetime in milliseconds
     */
    public JwtService(SecretKey signingKey, @Value("${jwt.expiration-ms}") long expirationMillis) {
        this.signingKey = signingKey;
        this.expirationMillis = expirationMillis;
    }

    /**
     * Generates a signed JWT for the given user id.
     *
     * @param userId user identifier to store as the subject
     * @return signed JWT string
     */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the subject (user id) from a JWT.
     *
     * @param token JWT string
     * @return subject stored in the token
     */
    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates that the token belongs to the given user id and is not expired.
     *
     * @param token JWT string
     * @param username expected subject value
     * @return true if valid, otherwise false
     */
    public boolean isTokenValid(String token, String username) {
        String tokenUserId = extractUserId(token);
        return tokenUserId.equals(username) && !isTokenExpired(token);
    }

    /**
     * Checks whether the token has expired.
     *
     * @param token JWT string
     * @return true if expired, otherwise false
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Parses and returns all claims from a signed JWT.
     *
     * @param token JWT string
     * @return token claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

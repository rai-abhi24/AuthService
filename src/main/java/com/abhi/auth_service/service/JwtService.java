package com.abhi.auth_service.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${jwt.access.token.expiry}")
    private Long accessTokenExpiryDuration;

    public String generateTokenFromEmail(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String email) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plusMillis(accessTokenExpiryDuration));

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSignKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        return extract(token, Claims::getSubject);
    }

    private <T> T extract(String token, Function<Claims, T> claimResolver) {
        final Claims claims = exctractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims exctractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // TODO: Improve this method
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

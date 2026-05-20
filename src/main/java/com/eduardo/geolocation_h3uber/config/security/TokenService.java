package com.eduardo.geolocation_h3uber.config.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eduardo.geolocation_h3uber.entities.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .issuer("api")
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("role", user.getRole().getRole())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000)) // 15 Minutos
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(UserEntity user) {
        return Jwts.builder()
                .issuer("api")
                .subject(user.getUsername())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 604800000L)) // 7 Dias
                .signWith(getSigningKey())
                .compact();
    }

    public Claims validateToken(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (!expectedType.equals(claims.get("type", String.class))) {
                return null;
            }
            return claims;
        } catch (Exception e) {
            return null;
        }
    }
}

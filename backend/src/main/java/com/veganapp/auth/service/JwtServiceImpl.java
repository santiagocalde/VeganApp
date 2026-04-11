package com.veganapp.auth.service;

import com.veganapp.common.config.AppProperties;
import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final AppProperties appProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = appProperties.jwt().secret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateAccessToken(Long userId, String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + appProperties.jwt().expirationMs());

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.debug("JWT expirado");
            return false;
        } catch (JwtException ex) {
            log.warn("JWT inválido: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public Long extractUserId(String token) {
        return Long.parseLong(extractClaims(token).getSubject());
    }

    @Override
    public String extractEmail(String token) {
        return extractClaims(token).get("email", String.class);
    }

    @Override
    public long getExpirationMs() {
        return appProperties.jwt().expirationMs();
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException ex) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }
}

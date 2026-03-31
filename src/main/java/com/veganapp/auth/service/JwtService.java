package com.veganapp.auth.service;

public interface JwtService {
    String generateAccessToken(Long userId, String email);

    boolean validateToken(String token);

    Long extractUserId(String token);

    String extractEmail(String token);

    long getExpirationMs();
}

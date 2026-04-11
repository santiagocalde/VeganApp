package com.veganapp.auth.service;

import java.util.Optional;

public interface RefreshTokenService {
    String createRefreshToken(Long userId);

    Optional<Long> validateAndGetUserId(String refreshToken);

    void revokeToken(String refreshToken);

    void revokeAllUserTokens(Long userId);
}

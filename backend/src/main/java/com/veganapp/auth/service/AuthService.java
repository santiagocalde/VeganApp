package com.veganapp.auth.service;

import com.veganapp.auth.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(String refreshToken);

    void logoutAllDevices(Long userId);

    void requestPasswordReset(String email);

    void resetPassword(PasswordResetRequest request);
}

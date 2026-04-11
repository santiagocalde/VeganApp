package com.veganapp.auth.service;

import com.veganapp.auth.dto.*;
import com.veganapp.user.entity.User;
import com.veganapp.user.repository.UserRepository;
import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(request.email().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name().trim())
                .profileType(request.profileType())
                .motivation(request.motivation())
                .experienceLevel(request.experienceLevel())
                .budgetLevel(request.budgetLevel())
                .tonePref(request.tonePref())
                .timezone(request.timezone())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Usuario registrado: id={}, motivation={}", savedUser.getId(), savedUser.getMotivation());

        return buildAuthResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository
                .findActiveByEmail(request.email().toLowerCase().trim())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Intento de login fallido para email={}", maskEmail(request.email()));
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        log.info("Login exitoso: userId={}", user.getId());
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        Long userId = refreshTokenService
                .validateAndGetUserId(request.refreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        refreshTokenService.revokeToken(request.refreshToken());

        log.info("Token renovado para userId={}", userId);
        return buildAuthResponse(user);
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
        log.info("Logout: refresh token revocado");
    }

    @Override
    public void logoutAllDevices(Long userId) {
        refreshTokenService.revokeAllUserTokens(userId);
        log.info("Logout en todos los dispositivos para userId={}", userId);
    }

    @Override
    public void requestPasswordReset(String email) {
        userRepository.findActiveByEmail(email.toLowerCase().trim()).ifPresent(user ->
                log.info("Password reset solicitado para userId={}", user.getId()));
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        throw new AppException(ErrorCode.INTERNAL_ERROR, "Función de recuperación disponible en Fase 2");
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getExpirationMs(),
                toUserInfo(user)
        );
    }

    private AuthResponse.UserInfo toUserInfo(User user) {
        return new AuthResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileType(),
                user.getMotivation(),
                user.getTonePref(),
                user.getExperienceLevel(),
                user.getBudgetLevel(),
                user.getTimezone()
        );
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        String local = parts[0];
        String masked = local.length() > 3
                ? local.substring(0, 3) + "***"
                : "***";
        return masked + "@" + parts[1];
    }
}

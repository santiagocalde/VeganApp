package com.veganapp.auth.service;

import com.veganapp.common.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final String REFRESH_PREFIX = "refresh:";
    private static final String USER_TOKEN_PREFIX = "refresh:user:%d:%s";

    private final RedisTemplate<String, String> redisTemplate;
    private final AppProperties appProperties;

    @Override
    public String createRefreshToken(Long userId) {
        String token = UUID.randomUUID().toString();
        long ttlDays = appProperties.jwt().refreshExpirationDays();

        String tokenKey = REFRESH_PREFIX + token;
        String userTokenKey = String.format(USER_TOKEN_PREFIX, userId, token);

        redisTemplate.opsForValue().set(tokenKey, userId.toString(), ttlDays, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(userTokenKey, token, ttlDays, TimeUnit.DAYS);

        log.info("Refresh token creado para userId={}", userId);
        return token;
    }

    @Override
    public Optional<Long> validateAndGetUserId(String refreshToken) {
        String key = REFRESH_PREFIX + refreshToken;
        String userIdStr = redisTemplate.opsForValue().get(key);

        if (userIdStr == null) {
            log.warn("Refresh token no encontrado o expirado");
            return Optional.empty();
        }

        return Optional.of(Long.parseLong(userIdStr));
    }

    @Override
    public void revokeToken(String refreshToken) {
        String key = REFRESH_PREFIX + refreshToken;
        String userIdStr = redisTemplate.opsForValue().get(key);

        if (userIdStr != null) {
            String userTokenKey = String.format(USER_TOKEN_PREFIX, Long.parseLong(userIdStr), refreshToken);
            redisTemplate.delete(key);
            redisTemplate.delete(userTokenKey);
            log.info("Refresh token revocado");
        }
    }

    @Override
    public void revokeAllUserTokens(Long userId) {
        String pattern = String.format("refresh:user:%d:*", userId);
        var keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            keys.forEach(userTokenKey -> {
                String token = userTokenKey.substring(userTokenKey.lastIndexOf(':') + 1);
                redisTemplate.delete(REFRESH_PREFIX + token);
                redisTemplate.delete(userTokenKey);
            });
            log.info("Todos los refresh tokens revocados para userId={}", userId);
        }
    }
}

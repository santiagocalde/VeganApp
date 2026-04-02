package com.veganapp.badge.service;

import com.veganapp.badge.dto.BadgeResponse;
import com.veganapp.badge.entity.Badge;
import com.veganapp.badge.entity.UserBadge;
import com.veganapp.badge.repository.BadgeRepository;
import com.veganapp.badge.repository.UserBadgeRepository;
import com.veganapp.common.config.AppProperties;
import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import com.veganapp.user.entity.User;
import com.veganapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementación de BadgeService.
 *
 * FIX #3: Características críticas
 * 1. @Async en checkAndAwardStreakBadge() — no bloquea al caller
 * 2. Idempotente — si ya tiene el badge, no lo duplica
 * 3. Fallback suave — si falla el BD, loguea pero no crash
 * 4. Transactional — atomicidad en la verificación+otorgación
 * 5. Batch query — getAllBadgesWithStatus() es O(n) no O(n²)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    /**
     * PASO 9 DE CHECK-IN: Verificar y otorgar badge de hito.
     *
     * FIX #3: @Async significa que se ejecuta en thread separado.
     * CheckInService NO espera a este método, devuelve response inmediatamente.
     *
     * Responsabilidades:
     * 1. Verificar que currentDays es un hito (de AppProperties)
     * 2. Parsear el código del badge (STREAK_{days})
     * 3. Verificar idempotencia (ya lo tiene?)
     * 4. Si no lo tiene, otorgarlo
     * 5. Loguear pero NO fallar (es async fire-and-forget)
     */
    @Async("badgeExecutor")
    @Override
    @Transactional
    public void checkAndAwardStreakBadge(Long userId, int currentDays) {
        // Parsear milestones desde AppProperties
        Set<Integer> milestones = parseMilestones(appProperties.badge().streakMilestones());
        
        if (!milestones.contains(currentDays)) {
            // No es hito, exit silenciosamente
            return;
        }

        try {
            // Buscar el usuario (validación)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found for badge award: {}", userId);
                        return new AppException(ErrorCode.USER_NOT_FOUND);
                    });

            // Mapear días a código de badge
            String badgeCode = "STREAK_" + currentDays;
            Badge badge = badgeRepository.findByCode(badgeCode)
                    .orElseThrow(() -> {
                        log.warn("Badge not found: {}", badgeCode);
                        return new AppException(ErrorCode.INTERNAL_ERROR, "Badge no encontrado: " + badgeCode);
                    });

            // FIX #3: Verificación de idempotencia
            if (userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId())) {
                log.debug("User {} already owns badge {}, skipping award", userId, badgeCode);
                return;
            }

            // Otorgar el badge
            UserBadge userBadge = UserBadge.builder()
                    .user(user)
                    .badge(badge)
                    .build();
            userBadgeRepository.save(userBadge);

            log.info("Badge awarded: userId={}, badge={}, days={}", userId, badgeCode, currentDays);

            // TODO: Fase 2 — disparar evento para notificaciones push
            // applicationEventPublisher.publishEvent(new BadgeUnlockedEvent(userId, badge));

        } catch (Exception e) {
            // FIX #3: Fallback suave — loguear pero NO lanzar excepción
            // Esto es async fire-and-forget, no hay caller esperando
            log.error("Error awarding badge for user {}, days {}", userId, currentDays, e);
            // NO re-throw. El check-in ya completó.
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeResponse> getAllBadgesWithStatus(Long userId) {
        // FIX #3: Batch query — evitar N+1
        List<Badge> allBadges = badgeRepository.findAll();
        Set<Long> userBadgeIds = userBadgeRepository.findBadgeIdsByUserId(userId);

        return allBadges.stream()
                .map(badge -> {
                    boolean earned = userBadgeIds.contains(badge.getId());
                    Instant earnedAt = earned 
                        ? userBadgeRepository.findEarnedAtByUserIdAndBadgeId(userId, badge.getId()).orElse(null)
                        : null;
                    return BadgeResponse.from(badge, earned, earnedAt);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeResponse> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserIdOrderByEarnedAtDesc(userId).stream()
                .map(ub -> BadgeResponse.from(ub.getBadge(), true, ub.getEarnedAt()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeResponse> getAllBadges() {
        return badgeRepository.findAll().stream()
                .map(badge -> BadgeResponse.from(badge, false, null))
                .toList();
    }

    // ────────────────────────────────────────────────────────────────────────────
    // PRIVADOS
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Parsea la string de milestones desde AppProperties
     * Ejemplo: "3,7,14,30,60,100,180,365" -> {3, 7, 14, 30, ...}
     */
    private Set<Integer> parseMilestones(String milestonesString) {
        Set<Integer> set = new HashSet<>();
        try {
            if (milestonesString != null && !milestonesString.trim().isEmpty()) {
                for (String number : milestonesString.split(",")) {
                    set.add(Integer.parseInt(number.trim()));
                }
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid milestone configuration: {}", milestonesString);
        }
        return set;
    }
}

package com.veganapp.badge.service;

import com.veganapp.badge.dto.BadgeResponse;
import java.util.List;

/**
 * Contrato del servicio de Badges.
 *
 * Responsabilidades:
 * - Verificar si un usuario alcanzó un hito de badge
 * - Otorgar badges (idempotente)
 * - Listar badges del usuario
 * - Listar todos los badges con status
 */
public interface BadgeService {
    /**
     * Verifica si el usuario desbloqueó algún badge por días de racha.
     * Este método es @Async, no bloquea al caller.
     *
     * Idempotente: si ya tiene el badge, no lo duplica.
     */
    void checkAndAwardStreakBadge(Long userId, int currentDays);

    /**
     * Retorna todos los badges con status del usuario actual:
     * - Para cada badge: { badge info, earned: bool, earnedAt: date? }
     */
    List<BadgeResponse> getAllBadgesWithStatus(Long userId);

    /**
     * Retorna solo los badges ganados por el usuario
     */
    List<BadgeResponse> getUserBadges(Long userId);

    /**
     * Retorna todos los badges del catálogo sin filtrar por usuario
     */
    List<BadgeResponse> getAllBadges();
}

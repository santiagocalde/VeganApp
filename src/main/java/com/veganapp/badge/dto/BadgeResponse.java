package com.veganapp.badge.dto;

import com.veganapp.badge.entity.Badge;
import java.time.Instant;

/**
 * DTO para respuesta de Badges.
 *
 * Usado en:
 * - GET /api/v1/badges (todos los badges con status del usuario)
 * - GET /api/v1/users/me/badges (badges ganados por el usuario)
 *
 * earnedAt: null si no fue ganado aún, populated si fue ganado
 */
public record BadgeResponse(
        Long id,
        String code,
        String name,
        String description,
        String iconUrl,
        String triggerType,      // enum name: STREAK_DAYS, TOTAL_POINTS, etc.
        int triggerValue,
        boolean earned,          // true si el usuario ya lo ganó
        Instant earnedAt         // null si earned=false, populated if earned=true
) {
    /**
     * Factory para crear respuesta desde Badge + earned status
     */
    public static BadgeResponse from(Badge badge, boolean earned, Instant earnedAt) {
        return new BadgeResponse(
                badge.getId(),
                badge.getCode(),
                badge.getName(),
                badge.getDescription(),
                badge.getIconUrl(),
                badge.getTriggerType().name(),
                badge.getTriggerValue(),
                earned,
                earnedAt
        );
    }
}

package com.veganapp.streak.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.veganapp.streak.entity.Streak;

/**
 * Respuesta del endpoint POST /api/v1/streaks/checkin (ROUTER §11).
 * Contiene el resultado del check-in del usuario tras los 11 pasos.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CheckInResponse(
        /** Cantidad de días consecutivos en la racha actual */
        int newDays,

        /** Puntos ganados en este check-in */
        int pointsEarned,

        /** Nivel actual del usuario (SEED, SPROUT, PLANT, TREE, FOREST) */
        Streak.StreakLevel currentLevel,

        /** Badge desbloqueado en este check-in, si aplica */
        BadgeInfo badgeUnlocked,

        /** Reacción de Sir Pippin (personalizada según contexto) */
        PippinReaction pippinReaction
) {

    /**
     * Información del badge desbloqueado.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BadgeInfo(
            String code,
            String name,
            String triggerMessage
    ) {}

    /**
     * Reacción de Sir Pippin con contexto narrativo.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PippinReaction(
            String message,
            String animationState,
            String context
    ) {}
}

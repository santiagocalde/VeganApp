package com.veganapp.streak.dto;

import jakarta.validation.constraints.Size;

/**
 * Cuerpo previsto para POST /api/v1/streaks/checkin (alineado a daily_logs.category en ROUTER §8).
 */
public record CheckInRequest(
        CheckInCategory category,

        @Size(max = 2000)
        String notes
) {
    public enum CheckInCategory {
        FOOD, RECIPE, REFLECTION, ACTIVITY
    }
}

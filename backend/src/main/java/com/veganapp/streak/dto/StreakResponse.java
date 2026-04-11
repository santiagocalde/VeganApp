package com.veganapp.streak.dto;

import java.time.LocalDate;

/**
 * DTO para respuestas de información de racha
 */
public record StreakResponse(
        Long userId,
        int currentDays,
        int bestDays,
        LocalDate lastCheckIn,
        LocalDate bestStreakStartDate,
        LocalDate bestStreakEndDate,
        boolean inDanger, // > 48h sin check-in
        int hoursToBreak  // Horas hasta que se rompa (si está en danger)
) {
}

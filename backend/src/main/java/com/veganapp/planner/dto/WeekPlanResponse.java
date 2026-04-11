package com.veganapp.planner.dto;

import com.veganapp.planner.entity.WeekPlan;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO para respuestas de WeekPlan
 */
public record WeekPlanResponse(
        Long id,
        LocalDate weekStartDate,
        boolean completed,
        Instant completedAt,
        Set<WeekPlanEntryResponse> entries,
        Instant createdAt,
        Instant updatedAt
) {
    public static WeekPlanResponse from(WeekPlan weekPlan, Set<WeekPlanEntryResponse> entries) {
        return new WeekPlanResponse(
                weekPlan.getId(),
                weekPlan.getWeekStartDate(),
                weekPlan.isCompleted(),
                weekPlan.getCompletedAt(),
                entries,
                weekPlan.getCreatedAt(),
                weekPlan.getUpdatedAt()
        );
    }
}

package com.veganapp.planner.dto;

import com.veganapp.planner.entity.WeekPlanEntry;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear un plan semanal
 */
public record WeekPlanCreateRequest(
        LocalDate weekStartDate,
        List<WeekPlanEntryCreateRequest> entries
) {
}

/**
 * DTO para agregar una entrada al plan
 */
public record WeekPlanEntryCreateRequest(
        DayOfWeek dayOfWeek,
        WeekPlanEntry.MealType mealType,
        Long recipeId,
        Integer servingsOverride
) {
}

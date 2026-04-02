package com.veganapp.planner.dto;

import com.veganapp.planner.entity.WeekPlanEntry;
import com.veganapp.recipe.dto.RecipeResponse;
import java.time.DayOfWeek;

/**
 * DTO para entries del weekly plan
 */
public record WeekPlanEntryResponse(
        Long id,
        DayOfWeek dayOfWeek,
        WeekPlanEntry.MealType mealType,
        RecipeResponse recipe,
        Integer servingsOverride
) {
    public static WeekPlanEntryResponse from(WeekPlanEntry entry) {
        return new WeekPlanEntryResponse(
                entry.getId(),
                entry.getDayOfWeek(),
                entry.getMealType(),
                RecipeResponse.from(entry.getRecipe()),
                entry.getServingsOverride()
        );
    }
}

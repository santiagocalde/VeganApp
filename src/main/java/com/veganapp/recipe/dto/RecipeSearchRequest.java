package com.veganapp.recipe.dto;

import com.veganapp.recipe.entity.Recipe.BudgetLevel;
import com.veganapp.recipe.entity.Recipe.DietType;
import com.veganapp.recipe.entity.Recipe.MealType;
import com.veganapp.recipe.entity.Recipe.RecipeDifficulty;
import com.veganapp.recipe.entity.RecipeTag.TagName;

import java.util.Set;

/**
 * DTO para búsqueda avanzada de recetas
 * 
 * POST /api/v1/recipes/search
 * Los campos son opcionales - usar AND logic para filtros múltiples
 */
public record RecipeSearchRequest(
        String title,                     // Búsqueda por título (LIKE)
        MealType mealType,               // Tipo de comida
        DietType dietType,               // Tipo de dieta
        RecipeDifficulty difficulty,     // Dificultad
        BudgetLevel budgetLevel,         // Presupuesto
        boolean glutenFree,              // Sin gluten
        boolean soyFree,                 // Sin soya
        Boolean highProtein,             // >= 15g proteína
        Boolean quickOnly,               // Tiempo total <= 30 min
        Integer maxCalories,             // Máximo de calorías
        Integer minCalories,             // Mínimo de calorías
        Set<TagName> tags                // Tags (ANY: OR logic)
) {
}

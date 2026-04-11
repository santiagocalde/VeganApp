package com.veganapp.recipe.dto;

import com.veganapp.recipe.entity.Ingredient;
import com.veganapp.recipe.entity.RecipeIngredient;
import com.veganapp.recipe.entity.RecipeTag;
import com.veganapp.recipe.entity.Recipe;
import com.veganapp.recipe.entity.Recipe.BudgetLevel;
import com.veganapp.recipe.entity.Recipe.DietType;
import com.veganapp.recipe.entity.Recipe.MealType;
import com.veganapp.recipe.entity.Recipe.RecipeDifficulty;
import com.veganapp.recipe.entity.RecipeTag.TagName;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO para respuestas de Recipe en GET endpoints
 */
public record RecipeResponse(
        Long id,
        String title,
        String description,
        String instructions,
        Integer prepTimeMinutes,
        Integer cookTimeMinutes,
        Integer servings,
        RecipeDifficulty difficulty,
        MealType mealType,
        DietType dietType,
        BudgetLevel budgetLevel,
        boolean glutenFree,
        boolean soyFree,
        Integer caloriesApprox,
        Double proteinG,
        Double ironMg,
        Double calciumMg,
        Double fatG,
        Double carbsG,
        String imageUrl,
        Set<IngredientResponse> ingredients,
        Set<TagName> tags
) {
    public static RecipeResponse from(Recipe recipe) {
        return new RecipeResponse(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getInstructions(),
                recipe.getPrepTimeMinutes(),
                recipe.getCookTimeMinutes(),
                recipe.getServings(),
                recipe.getDifficulty(),
                recipe.getMealType(),
                recipe.getDietType(),
                recipe.getBudgetLevel(),
                recipe.isGlutenFree(),
                recipe.isSoyFree(),
                recipe.getCaloriesApprox(),
                recipe.getProteinG(),
                recipe.getIronMg(),
                recipe.getCalciumMg(),
                recipe.getFatG(),
                recipe.getCarbsG(),
                recipe.getImageUrl(),
                recipe.getIngredients().stream()
                        .map(IngredientResponse::from)
                        .collect(Collectors.toSet()),
                recipe.getTags().stream()
                        .map(RecipeTag::getTagName)
                        .collect(Collectors.toSet())
        );
    }

    public record IngredientResponse(
            Long ingredientId,
            String name,
            Double quantity,
            String unit,
            String notes
    ) {
        public static IngredientResponse from(RecipeIngredient ri) {
            return new IngredientResponse(
                    ri.getIngredient().getId(),
                    ri.getIngredient().getName(),
                    ri.getQuantity(),
                    ri.getUnit(),
                    ri.getNotes()
            );
        }
    }
}

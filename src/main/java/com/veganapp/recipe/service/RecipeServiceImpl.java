package com.veganapp.recipe.service;

import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import com.veganapp.recipe.dto.RecipeResponse;
import com.veganapp.recipe.dto.RecipeSearchRequest;
import com.veganapp.recipe.entity.Recipe;
import com.veganapp.recipe.entity.Recipe.DietType;
import com.veganapp.recipe.entity.Recipe.MealType;
import com.veganapp.recipe.entity.Recipe.RecipeDifficulty;
import com.veganapp.recipe.repository.RecipeRepository;
import com.veganapp.recipe.repository.RecipeTagRepository;
import com.veganapp.user.entity.User;
import com.veganapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación de RecipeService.
 *
 * Responsabilidades:
 * - Búsqueda avanzada con filtros múltiples
 * - Recomendaciones personalizadas basadas en perfil del usuario
 * - CRUD básico con auditoría
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeTagRepository recipeTagRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<RecipeResponse> getRecipeById(Long id) {
        return recipeRepository.findByIdAndActiveTrue(id)
                .map(RecipeResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> getAllActiveRecipes() {
        return recipeRepository.findAll().stream()
                .filter(Recipe::isActive)
                .map(RecipeResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> searchRecipes(RecipeSearchRequest request) {
        // Empezar con todas las recetas activas
        List<Recipe> recipes = recipeRepository.findAll().stream()
                .filter(Recipe::isActive)
                .collect(Collectors.toList());

        // Aplicar filtros AND (todos deben coincidir)
        
        // Filtro por título
        if (request.title() != null && !request.title().isEmpty()) {
            String titleLower = request.title().toLowerCase();
            recipes = recipes.stream()
                    .filter(r -> r.getTitle().toLowerCase().contains(titleLower))
                    .collect(Collectors.toList());
        }

        // Filtro por tipo de comida
        if (request.mealType() != null) {
            recipes = recipes.stream()
                    .filter(r -> r.getMealType() == request.mealType())
                    .collect(Collectors.toList());
        }

        // Filtro por tipo de dieta
        if (request.dietType() != null) {
            recipes = recipes.stream()
                    .filter(r -> r.getDietType() == request.dietType())
                    .collect(Collectors.toList());
        }

        // Filtro por dificultad
        if (request.difficulty() != null) {
            recipes = recipes.stream()
                    .filter(r -> r.getDifficulty() == request.difficulty())
                    .collect(Collectors.toList());
        }

        // Filtro por presupuesto
        if (request.budgetLevel() != null) {
            recipes = recipes.stream()
                    .filter(r -> r.getBudgetLevel() == request.budgetLevel())
                    .collect(Collectors.toList());
        }

        // Filtro sin gluten
        if (request.glutenFree()) {
            recipes = recipes.stream()
                    .filter(Recipe::isGlutenFree)
                    .collect(Collectors.toList());
        }

        // Filtro sin soya
        if (request.soyFree()) {
            recipes = recipes.stream()
                    .filter(Recipe::isSoyFree)
                    .collect(Collectors.toList());
        }

        // Filtro proteína alta
        if (Boolean.TRUE.equals(request.highProtein())) {
            recipes = recipes.stream()
                    .filter(r -> r.getProteinG() != null && r.getProteinG() >= 15)
                    .collect(Collectors.toList());
        }

        // Filtro recetas rápidas
        if (Boolean.TRUE.equals(request.quickOnly())) {
            recipes = recipes.stream()
                    .filter(r -> {
                        int totalTime = (r.getPrepTimeMinutes() != null ? r.getPrepTimeMinutes() : 0) +
                                (r.getCookTimeMinutes() != null ? r.getCookTimeMinutes() : 0);
                        return totalTime <= 30;
                    })
                    .collect(Collectors.toList());
        }

        // Filtro rango de calorías
        if (request.minCalories() != null) {
            recipes = recipes.stream()
                    .filter(r -> r.getCaloriesApprox() != null && r.getCaloriesApprox() >= request.minCalories())
                    .collect(Collectors.toList());
        }
        if (request.maxCalories() != null) {
            recipes = recipes.stream()
                    .filter(r -> r.getCaloriesApprox() != null && r.getCaloriesApprox() <= request.maxCalories())
                    .collect(Collectors.toList());
        }

        // Filtro por tags (OR logic - cualquier tag coincide)
        if (request.tags() != null && !request.tags().isEmpty()) {
            Set<Long> recipeIdsWithTags = request.tags().stream()
                    .flatMap(tagName -> recipeTagRepository.findByTagName(tagName).stream()
                            .map(rt -> rt.getRecipe().getId()))
                    .collect(Collectors.toSet());
            
            recipes = recipes.stream()
                    .filter(r -> recipeIdsWithTags.contains(r.getId()))
                    .collect(Collectors.toList());
        }

        return recipes.stream()
                .map(RecipeResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> searchByTitle(String title) {
        return recipeRepository.findActiveByTitleContainsIgnoreCase(title).stream()
                .map(RecipeResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> getRecommendedRecipes(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for recommendations: {}", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        // Recomendaciones basadas en:
        // 1. Tipo de dieta del usuario
        // 2. Presupuesto del usuario
        // 3. Motivación del usuario (si es HEALTH -> alta proteína, si es FITNESS -> alto contenido nutricional)

        RecipeSearchRequest searchRequest = new RecipeSearchRequest(
                null,                                    // title
                null,                                    // mealType
                user.getProfileType().equals("VEGAN") 
                        ? DietType.VEGAN 
                        : DietType.VEGETARIAN,           // dietType
                null,                                    // difficulty
                user.getBudgetLevel(),                   // budgetLevel
                false,                                   // glutenFree
                false,                                   // soyFree
                user.getMotivation().equals("HEALTH") ? true : null,  // highProtein si es HEALTH
                null,                                    // quickOnly
                null,                                    // minCalories
                null,                                    // maxCalories
                null                                     // tags
        );

        return searchRecipes(searchRequest).stream()
                .limit(limit)
                .toList();
    }

    @Override
    @Transactional
    public RecipeResponse createRecipe(Recipe recipe) {
        Recipe saved = recipeRepository.save(recipe);
        log.info("Recipe created: id={}, title={}", saved.getId(), saved.getTitle());
        return RecipeResponse.from(saved);
    }

    @Override
    @Transactional
    public RecipeResponse updateRecipe(Long id, Recipe recipe) {
        Recipe existing = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Recipe not found for update: {}", id);
                    return new AppException(ErrorCode.INTERNAL_ERROR, "Receta no encontrada");
                });

        // Actualizar campos
        if (recipe.getTitle() != null) existing.setTitle(recipe.getTitle());
        if (recipe.getDescription() != null) existing.setDescription(recipe.getDescription());
        if (recipe.getInstructions() != null) existing.setInstructions(recipe.getInstructions());
        if (recipe.getDifficulty() != null) existing.setDifficulty(recipe.getDifficulty());
        // ... más campos ...

        Recipe saved = recipeRepository.save(existing);
        log.info("Recipe updated: id={}", id);
        return RecipeResponse.from(saved);
    }

    @Override
    @Transactional
    public void deleteRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Recipe not found for deletion: {}", id);
                    return new AppException(ErrorCode.INTERNAL_ERROR, "Receta no encontrada");
                });

        recipe.setActive(false);
        recipeRepository.save(recipe);
        log.info("Recipe deleted (soft): id={}", id);
    }
}

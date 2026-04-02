package com.veganapp.recipe.service;

import com.veganapp.recipe.dto.RecipeResponse;
import com.veganapp.recipe.dto.RecipeSearchRequest;
import com.veganapp.recipe.entity.Recipe;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del servicio de Recetas.
 *
 * Responsabilidades:
 * - CRUD básico
 * - Búsqueda avanzada con múltiples filtros
 * - Cálculos nutricionales
 * - Recomendaciones contextuales
 */
public interface RecipeService {

    /**
     * Obtiene una receta activa por ID
     */
    Optional<RecipeResponse> getRecipeById(Long id);

    /**
     * Lista todas las recetas activas
     */
    List<RecipeResponse> getAllActiveRecipes();

    /**
     * Busca recetas con filtros avanzados (AND logic)
     */
    List<RecipeResponse> searchRecipes(RecipeSearchRequest request);

    /**
     * Busca recetas por título
     */
    List<RecipeResponse> searchByTitle(String title);

    /**
     * Obtiene recetas recomendadas para un usuario
     * (basadas en motivación, presupuesto, tone)
     */
    List<RecipeResponse> getRecommendedRecipes(Long userId, int limit);

    /**
     * Crea una nueva receta (admin only)
     */
    RecipeResponse createRecipe(Recipe recipe);

    /**
     * Actualiza una receta existente (admin only)
     */
    RecipeResponse updateRecipe(Long id, Recipe recipe);

    /**
     * Marca una receta como inactiva (soft delete, admin only)
     */
    void deleteRecipe(Long id);
}

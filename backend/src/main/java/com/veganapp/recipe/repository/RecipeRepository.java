package com.veganapp.recipe.repository;

import com.veganapp.recipe.entity.Recipe;
import com.veganapp.recipe.entity.Recipe.DietType;
import com.veganapp.recipe.entity.Recipe.MealType;
import com.veganapp.recipe.entity.Recipe.RecipeDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Busca recetas activas por título (LIKE case-insensitive)
     */
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND LOWER(r.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Recipe> findActiveByTitleContainsIgnoreCase(@Param("title") String title);

    /**
     * Busca recetas activas por tipo de comida
     */
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.mealType = :mealType")
    List<Recipe> findActiveByMealType(@Param("mealType") MealType mealType);

    /**
     * Busca recetas activas por tipo de dieta
     */
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.dietType = :dietType")
    List<Recipe> findActiveByDietType(@Param("dietType") DietType dietType);

    /**
     * Busca recetas activas sin gluten
     */
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.glutenFree = true")
    List<Recipe> findActiveGlutenFree();

    /**
     * Busca recetas activas sin soya
     */
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.soyFree = true")
    List<Recipe> findActiveSoyFree();

    /**
     * Busca recetas activas rápidas (prep + cook <= 30 minutos)
     */
    @Query("""
            SELECT r FROM Recipe r 
            WHERE r.active = true 
            AND (COALESCE(r.prepTimeMinutes, 0) + COALESCE(r.cookTimeMinutes, 0)) <= 30
            """)
    List<Recipe> findActiveQuickRecipes();

    /**
     * Busca recetas activas por dificultad
     */
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.difficulty = :difficulty")
    List<Recipe> findActiveByDifficulty(@Param("difficulty") RecipeDifficulty difficulty);

    /**
     * Busca recetas activas ricas en proteína (>= 15g por serving)
     */
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.proteinG >= 15")
    List<Recipe> findActiveHighProtein();

    /**
     * Busca recetas activas ricas en hierro (>= 2mg por serving)
     */
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.ironMg >= 2")
    List<Recipe> findActiveRichIron();

    /**
     * Busca recetas activas con calorías en rango
     */
    @Query("""
            SELECT r FROM Recipe r 
            WHERE r.active = true 
            AND r.caloriesApprox >= :minCalories 
            AND r.caloriesApprox <= :maxCalories
            """)
    List<Recipe> findActiveByCalorieRange(
            @Param("minCalories") Integer minCalories,
            @Param("maxCalories") Integer maxCalories
    );

    /**
     * Busca una receta activa por ID
     */
    Optional<Recipe> findByIdAndActiveTrue(Long id);
}

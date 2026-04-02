package com.veganapp.recipe.controller;

import com.veganapp.common.dto.ApiResponse;
import com.veganapp.recipe.dto.RecipeResponse;
import com.veganapp.recipe.dto.RecipeSearchRequest;
import com.veganapp.recipe.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * GET /api/v1/recipes
     *
     * Retorna todas las recetas activas
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> getAllRecipes() {
        return ResponseEntity.ok(
                ApiResponse.success(recipeService.getAllActiveRecipes())
        );
    }

    /**
     * GET /api/v1/recipes/{id}
     *
     * Obtiene una receta específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecipeResponse>> getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(recipe -> ResponseEntity.ok(ApiResponse.success(recipe)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/recipes/search?title=pasta
     *
     * Busca recetas por título
     */
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> searchByTitle(
            @RequestParam String title
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(recipeService.searchByTitle(title))
        );
    }

    /**
     * POST /api/v1/recipes/search
     *
     * Búsqueda avanzada con múltiples filtros (AND logic)
     * Request: RecipeSearchRequest con filtros opcionales
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> searchRecipes(
            @Valid @RequestBody RecipeSearchRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(recipeService.searchRecipes(request))
        );
    }

    /**
     * GET /api/v1/recipes/recommended
     *
     * Retorna recetas recomendadas para el usuario autenticado
     * Basadas en: tipo de dieta, presupuesto, motivación
     */
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> getRecommendedRecipes(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(recipeService.getRecommendedRecipes(userId, limit))
        );
    }
}

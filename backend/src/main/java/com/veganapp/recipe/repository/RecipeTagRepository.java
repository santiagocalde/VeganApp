package com.veganapp.recipe.repository;

import com.veganapp.recipe.entity.RecipeTag;
import com.veganapp.recipe.entity.RecipeTag.TagName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeTagRepository extends JpaRepository<RecipeTag, Long> {

    /**
     * Busca todas las recetas con un tag específico (activas)
     */
    @Query("""
            SELECT rt.recipe FROM RecipeTag rt 
            WHERE rt.tagName = :tagName 
            AND rt.recipe.active = true
            """)
    List<RecipeTag> findByTagName(@Param("tagName") TagName tagName);

    /**
     * Busca tags de una receta específica
     */
    @Query("SELECT rt FROM RecipeTag rt WHERE rt.recipe.id = :recipeId")
    List<RecipeTag> findByRecipeId(@Param("recipeId") Long recipeId);
}

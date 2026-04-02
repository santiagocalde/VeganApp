package com.veganapp.recipe.repository;

import com.veganapp.recipe.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * Busca ingrediente por nombre
     */
    Optional<Ingredient> findByNameIgnoreCase(String name);

    /**
     * Busca ingredientes por categoría
     */
    @Query("SELECT i FROM Ingredient i WHERE LOWER(i.category) = LOWER(:category)")
    List<Ingredient> findByCategory(@Param("category") String category);

    /**
     * Busca ingredientes veganos
     */
    @Query("SELECT i FROM Ingredient i WHERE i.vegan = true")
    List<Ingredient> findVegan();

    /**
     * Busca ingredientes sin gluten
     */
    @Query("SELECT i FROM Ingredient i WHERE i.glutenFree = true")
    List<Ingredient> findGlutenFree();

    /**
     * Busca ingredientes sin soya
     */
    @Query("SELECT i FROM Ingredient i WHERE i.soy = false")
    List<Ingredient> findSoyFree();
}

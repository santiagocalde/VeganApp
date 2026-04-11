package com.veganapp.recipe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Entity RecipeIngredient: join entre Recipe e Ingredient
 *
 * Incluye cantidad específica para la receta (ej: 200g de tomate para esa receta)
 */
@Entity
@Table(name = "recipe_ingredients", indexes = {
        @Index(name = "idx_recipe_ingredients_recipe_id", columnList = "recipe_id"),
        @Index(name = "idx_recipe_ingredients_ingredient_id", columnList = "ingredient_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;

    @Column(length = 50)
    private String unit; // "g", "ml", "cup", puedo ser diferente al del ingredient

    @Column(length = 500)
    private String notes; // "picado", "cocido", "crudo", etc.

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

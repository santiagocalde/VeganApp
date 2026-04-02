package com.veganapp.recipe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity Ingrediente: catálogo de ingredientes únicos
 *
 * Cada ingrediente tiene propiedades nutricionales por unidad (100g)
 */
@Entity
@Table(name = "ingredients", indexes = {
        @Index(name = "idx_ingredients_name", columnList = "name")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(length = 50)
    private String category;

    @Column(length = 50)
    private String unit; // "g", "ml", "cup", "tsp", etc.

    // Valores nutricionales por 100g
    @Column(name = "calories_per_100g")
    private Integer caloriesPer100g;

    @Column(name = "protein_g_per_100g")
    private Double proteinGPer100g;

    @Column(name = "fat_g_per_100g")
    private Double fatGPer100g;

    @Column(name = "carbs_g_per_100g")
    private Double carbsGPer100g;

    @Column(name = "fiber_g_per_100g")
    private Double fiberGPer100g;

    @Column(name = "iron_mg_per_100g")
    private Double ironMgPer100g;

    @Column(name = "calcium_mg_per_100g")
    private Double calciumMgPer100g;

    @Column(name = "is_vegan", nullable = false)
    @Builder.Default
    private boolean vegan = true;

    @Column(name = "is_gluten_free", nullable = false)
    @Builder.Default
    private boolean glutenFree = false;

    @Column(name = "is_soy", nullable = false)
    @Builder.Default
    private boolean soy = false;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

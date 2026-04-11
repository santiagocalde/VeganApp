package com.veganapp.recipe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity Receta: representa una receta vegana/vegetariana
 *
 * Campos nutricionales y metadatos para búsqueda/filtros
 */
@Entity
@Table(name = "recipes", indexes = {
        @Index(name = "idx_recipes_title", columnList = "title"),
        @Index(name = "idx_recipes_meal_type", columnList = "meal_type"),
        @Index(name = "idx_recipes_is_active", columnList = "is_active")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;

    @Column(nullable = false)
    @Builder.Default
    private Integer servings = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Enumerated(EnumType.STRING)
    @Column(name = "diet_type", nullable = false)
    private DietType dietType;

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_level")
    private BudgetLevel budgetLevel;

    @Column(name = "is_gluten_free", nullable = false)
    @Builder.Default
    private boolean glutenFree = false;

    @Column(name = "is_soy_free", nullable = false)
    @Builder.Default
    private boolean soyFree = false;

    @Column(name = "calories_approx")
    private Integer caloriesApprox;

    @Column(name = "protein_g")
    private Double proteinG;

    @Column(name = "iron_mg")
    private Double ironMg;

    @Column(name = "calcium_mg")
    private Double calciumMg;

    @Column(name = "fat_g")
    private Double fatG;

    @Column(name = "carbs_g")
    private Double carbsG;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RecipeIngredient> ingredients = new HashSet<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RecipeTag> tags = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum RecipeDifficulty { EASY, MEDIUM, HARD }
    public enum MealType { BREAKFAST, LUNCH, DINNER, SNACK, DESSERT }
    public enum DietType { VEGAN, VEGETARIAN, FLEXITARIAN }
    public enum BudgetLevel { LOW, NORMAL, HIGH }
}

package com.veganapp.recipe.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity RecipeTag: tags para búsqueda y filtros
 *
 * Ejemplos: HIGH_PROTEIN, RICH_IRON, GLUTEN_FREE, LOW_CALORIE, QUICK, BUDGET_FRIENDLY
 */
@Entity
@Table(name = "recipe_tags", indexes = {
        @Index(name = "idx_recipe_tags_recipe_id", columnList = "recipe_id"),
        @Index(name = "idx_recipe_tags_tag_name", columnList = "tag_name")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_name", nullable = false, length = 50)
    private TagName tagName;

    public enum TagName {
        HIGH_PROTEIN,      // >= 15g proteína por serving
        HIGH_FIBER,        // >= 5g fibra por serving
        HIGH_IRON,         // >= 2mg hierro por serving
        HIGH_CALCIUM,      // >= 150mg calcio por serving
        LOW_CALORIE,       // < 300 kcal por serving
        QUICK,             // Total time <= 30 min
        BUDGET_FRIENDLY,   // Budget LOW
        BUDGET_NORMAL,     // Budget NORMAL
        NO_COOKING,        // No requiere cocción
        FREEZER_FRIENDLY,  // Se puede congelar
        KIDS_FRIENDLY      // Apto para niños
    }
}

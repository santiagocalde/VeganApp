package com.veganapp.planner.entity;

import com.veganapp.recipe.entity.Recipe;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.DayOfWeek;

/**
 * Entity WeekPlanEntry: una entrada individual del plan semanal
 *
 * Ejemplo: Lunes - Desayuno - RecetaX, Lunes - Almuerzo - RecetaY, etc.
 */
@Entity
@Table(name = "week_plan_entries", indexes = {
        @Index(name = "idx_week_plan_entries_week_plan_id", columnList = "week_plan_id"),
        @Index(name = "idx_week_plan_entries_recipe_id", columnList = "recipe_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeekPlanEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "week_plan_id", nullable = false)
    private WeekPlan weekPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek; // MONDAY, TUESDAY, ...

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 50)
    private MealType mealType; // BREAKFAST, LUNCH, DINNER, SNACK

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "servings_override")
    private Integer servingsOverride; // Si null, usar servings de la receta

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public enum MealType {
        BREAKFAST,
        LUNCH,
        DINNER,
        SNACK
    }
}

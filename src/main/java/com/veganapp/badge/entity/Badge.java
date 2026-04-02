package com.veganapp.badge.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity: Catálogo de Badges (logros).
 *
 * Cada badge tiene:
 * - Código único (STREAK_3, STREAK_7, etc.)
 * - Meta a alcanzar (triggerValue = días de racha, puntos, etc.)
 * - Tipo de trigger (qué evento lo dispara)
 *
 * Un usuario puede ganar múltiples badges de los mismos (no, unique constraint lo previene).
 */
@Entity
@Table(
    name = "badges",
    uniqueConstraints = @UniqueConstraint(columnNames = "code"),
    indexes = @Index(name = "idx_badge_code", columnList = "code")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private TriggerType triggerType;

    @Column(name = "trigger_value", nullable = false)
    private int triggerValue;

    public enum TriggerType {
        STREAK_DAYS,          // Se gana al alcanzar X días de racha
        TOTAL_POINTS,         // Se gana al acumular X puntos totales
        RECIPES_SAVED,        // Se gana al guardarel receta #X
        PLANNER_COMPLETED,    // Se gana al completar X planificadores semanales
        SPECIAL               // Eventos especiales
    }
}

package com.veganapp.plate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity: Opciones para armar platos (base, proteína, verdura, salsa).
 *
 * Permite que los usuarios combinen ingredientes según su objetivo nutritivo.
 * La BD tiene 4 categorías y el usuario elige 1 de cada.
 *
 * Ejemplo:
 * - BASE: Arroz, Quinoa, Pasta
 * - PROTEIN: Tofu, Lentejas, Garbanzo
 * - VEGETABLE: Brócoli, Espinaca, Zanahoria
 * - SAUCE: Palta, Hummus, Tahini
 *
 * FIX #5: PlateOption incluye validación de categoría en PlateBuilderService
 */
@Entity
@Table(
    name = "plate_options",
    indexes = @Index(name = "idx_plate_options_category", columnList = "category")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlateOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlateCategory category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private Integer calories;

    @Column(name = "protein_g", precision = 5, scale = 1)
    private BigDecimal proteinG;

    @Column(name = "fat_g", precision = 5, scale = 1)
    private BigDecimal fatG;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    public enum PlateCategory {
        BASE,      // Arroz, Quinoa, Pasta, Papa, Batata
        PROTEIN,   // Tofu, Lentejas, Garbanzo, Tempeh, Seitán
        VEGETABLE, // Brócoli, Espinaca, Zanahoria, Zucchini
        SAUCE      // Palta, Hummus, Tahini, Aceite
    }
}

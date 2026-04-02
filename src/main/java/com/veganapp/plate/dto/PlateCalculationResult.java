package com.veganapp.plate.dto;

import com.veganapp.pippin.dto.PippinReactionResponse;
import java.math.BigDecimal;

/**
 * DTO: Resultado del cálculo nutritivo de un plato armado.
 *
 * Value Object: immutable, sin identidad de negocio, representa el resultado
 * del cálculo de macros y balance del plato.
 *
 * isBalanced = true cuando:
 *   - Calorías totales entre 350 y 750 (rango de plato principal saludable)
 *   - Proteína >= 15g (mínimo para saciedad y mantenimiento muscular)
 *   - Grasa <= 28g (no excesiva para una comida vegana)
 *
 * Sir Pippin comenta sobre el balance en pippinReaction.
 */
public record PlateCalculationResult(
        PlateOptionResponse base,
        PlateOptionResponse protein,
        PlateOptionResponse vegetable,
        PlateOptionResponse sauce,
        int totalCalories,
        BigDecimal totalProteinG,
        BigDecimal totalFatG,
        boolean isBalanced,
        String balanceMessage,        // explicación del balance o desequilibrio
        PippinReactionResponse pippinReaction
) {}

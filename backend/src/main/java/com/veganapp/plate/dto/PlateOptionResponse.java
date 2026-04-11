package com.veganapp.plate.dto;

import com.veganapp.plate.entity.PlateOption.PlateCategory;
import java.math.BigDecimal;

/**
 * DTO: Respuesta con los datos de una opción de plato.
 */
public record PlateOptionResponse(
        Long id,
        PlateCategory category,
        String name,
        Integer calories,
        BigDecimal proteinG,
        BigDecimal fatG,
        String iconUrl
) {}

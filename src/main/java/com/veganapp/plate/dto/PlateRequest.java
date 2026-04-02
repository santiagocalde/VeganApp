package com.veganapp.plate.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO: Request para calcular un plato.
 *
 * Todos los campos son obligatorios: el plato necesita las 4 categorías.
 * Esto es Fail Fast: si falta una, error 400 antes de consultar la DB.
 *
 * FIX #5: Validación en @NotNull — Spring Bean Validation
 */
public record PlateRequest(
        @NotNull(message = "Seleccioná una base (arroz, quinoa, pasta, etc.)")
        Long baseId,

        @NotNull(message = "Seleccioná una proteína (tofu, lentejas, garbanzo, etc.)")
        Long proteinId,

        @NotNull(message = "Seleccioná una verdura")
        Long vegetableId,

        @NotNull(message = "Seleccioná una salsa o grasa saludable")
        Long sauceId
) {}

package com.veganapp.plate.service;

import com.veganapp.plate.dto.PlateCalculationResult;
import com.veganapp.plate.dto.PlateOptionResponse;
import com.veganapp.plate.dto.PlateRequest;
import com.veganapp.plate.entity.PlateOption.PlateCategory;

import java.util.List;

/**
 * Contrato del servicio de Armado de Platos.
 *
 * Responsabilidades:
 * - Listar opciones por categoría (para que el usuario elija)
 * - Calcular macros del plato (proteína, calorías, grasa)
 * - Evaluar si el plato es nutricionalmente balanceado
 * - Generar comentario de Sir Pippin sobre la elección
 */
public interface PlateBuilderService {
    /**
     * Devuelve todas las opciones activas para una categoría
     */
    List<PlateOptionResponse> getOptionsByCategory(PlateCategory category);

    /**
     * Calcula el plato completo: obtiene las 4 opciones, suma macros,
     * evalúa balance, y genera reacción de Sir Pippin.
     *
     * FIX #5: Validación robusta
     * - Fail Fast: si algún ID es null o no existe, error 400/404 inmediato
     * - Validar categoría: asegurar que baseId es BASE, proteinId es PROTEIN, etc.
     * - Rango numérico: IDs deben ser positivos
     */
    PlateCalculationResult calculatePlate(PlateRequest request, Long userId);
}

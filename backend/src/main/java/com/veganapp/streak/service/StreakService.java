package com.veganapp.streak.service;

import com.veganapp.streak.dto.StreakResponse;

import java.util.Optional;

/**
 * Contrato del servicio de Racha.
 *
 * Responsabilidades:
 * - Obtener información de racha actual
 * - Obtener mejor racha histórica
 * - Determinar si está en peligro
 */
public interface StreakService {

    /**
     * Obtiene información de racha del usuario
     */
    Optional<StreakResponse> getStreakInfo(Long userId);

    /**
     * Obtiene la información de racha para mostrar en el dashboard
     */
    StreakResponse getStreakInfoOrDefault(Long userId);
}

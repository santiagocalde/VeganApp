package com.veganapp.streak.service;

import com.veganapp.streak.dto.CheckInRequest;
import com.veganapp.streak.dto.CheckInResponse;

/**
 * Servicio de check-in diario — Algoritmo de 11 pasos (ROUTER §11).
 * Define el contrato para registrar el check-in del usuario después de
 * un día completado en su transición/mantenimiento vegano.
 */
public interface CheckInService {

    /**
     * Realiza el check-in del usuario.
     * Implementa el algoritmo exacto de 11 pasos descrito en ROUTER §11:
     *
     * 1.  Obtener fecha LOCAL del usuario (timezone)
     * 2.  Verificar duplicado de check-in hoy
     * 3.  Verificar pausa activa (no penalizar)
     * 4.  Calcular continuidad de racha
     * 5.  Calcular multiplicador de puntos
     * 6.  @Transactional: guardar DailyLog + actualizar Streak
     * 7.  Actualizar record_days
     * 8.  Calcular nivel por total_points
     * 9.  Verificar hito de badge (días: 3, 7, 14, 30, 60, 100, 180, 365)
     * 10. Obtener reacción de Sir Pippin
     * 11. Devolver CheckInResponse completa
     *
     * @param userId  ID del usuario autenticado
     * @param request Cuerpo del check-in (categoría, notas opcionales)
     * @return CheckInResponse con nuevos días, puntos, level, badge (si) y reacción de Pippin
     * @throws com.veganapp.common.exception.AppException si el usuario no existe,
     *         ya hizo check-in hoy, o hay otro error de validación
     */
    CheckInResponse checkIn(Long userId, CheckInRequest request);
}

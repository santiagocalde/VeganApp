package com.veganapp.planner.service;

import com.veganapp.planner.dto.WeekPlanCreateRequest;
import com.veganapp.planner.dto.WeekPlanResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrato del servicio de Planificación Semanal.
 *
 * Responsabilidades:
 * - CRUD de planes semanales
 * - Crear planes con múltiples entries
 * - Marcar plan como completado
 * - Generar shopping list desde plan
 */
public interface PlannerService {

    /**
     * Obtiene o crea un plan semanal para el usuario
     */
    WeekPlanResponse getOrCreateWeekPlan(Long userId, LocalDate weekStartDate);

    /**
     * Crea un nuevo plan semanal con entries
     */
    WeekPlanResponse createWeekPlan(Long userId, WeekPlanCreateRequest request);

    /**
     * Obtiene un plan existente
     */
    Optional<WeekPlanResponse> getWeekPlan(Long weekPlanId, Long userId);

    /**
     * Obtiene el plan más reciente del usuario
     */
    Optional<WeekPlanResponse> getLatestWeekPlan(Long userId);

    /**
     * Lista todos los planes del usuario
     */
    List<WeekPlanResponse> getAllWeekPlans(Long userId);

    /**
     * Marca un plan como completado
     */
    WeekPlanResponse markAsCompleted(Long weekPlanId, Long userId);

    /**
     * Elimina un plan y sus entries
     */
    void deleteWeekPlan(Long weekPlanId, Long userId);
}

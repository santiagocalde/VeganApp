package com.veganapp.planner.controller;

import com.veganapp.common.dto.ApiResponse;
import com.veganapp.planner.dto.WeekPlanCreateRequest;
import com.veganapp.planner.dto.WeekPlanResponse;
import com.veganapp.planner.service.PlannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/planner")
@RequiredArgsConstructor
public class PlannerController {

    private final PlannerService plannerService;

    /**
     * GET /api/v1/planner/week?date=2024-01-01
     *
     * Obtiene o crea el plan semanal para una fecha específica
     */
    @GetMapping("/week")
    public ResponseEntity<ApiResponse<WeekPlanResponse>> getOrCreateWeekPlan(
            @AuthenticationPrincipal Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(plannerService.getOrCreateWeekPlan(userId, date))
        );
    }

    /**
     * POST /api/v1/planner/week
     *
     * Crea un nuevo plan semanal con entries
     * Request: { weekStartDate, entries: [ { dayOfWeek, mealType, recipeId, servingsOverride } ] }
     */
    @PostMapping("/week")
    public ResponseEntity<ApiResponse<WeekPlanResponse>> createWeekPlan(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WeekPlanCreateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(plannerService.createWeekPlan(userId, request))
        );
    }

    /**
     * GET /api/v1/planner/latest
     *
     * Obtiene el plan semanal más reciente del usuario
     */
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<WeekPlanResponse>> getLatestWeekPlan(
            @AuthenticationPrincipal Long userId
    ) {
        return plannerService.getLatestWeekPlan(userId)
                .map(plan -> ResponseEntity.ok(ApiResponse.success(plan)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * GET /api/v1/planner/all
     *
     * Lista todos los planes del usuario
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<WeekPlanResponse>>> getAllWeekPlans(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(plannerService.getAllWeekPlans(userId))
        );
    }

    /**
     * PUT /api/v1/planner/{weekPlanId}/complete
     *
     * Marca un plan como completado
     */
    @PutMapping("/{weekPlanId}/complete")
    public ResponseEntity<ApiResponse<WeekPlanResponse>> markAsCompleted(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long weekPlanId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(plannerService.markAsCompleted(weekPlanId, userId))
        );
    }

    /**
     * DELETE /api/v1/planner/{weekPlanId}
     *
     * Elimina un plan semanal
     */
    @DeleteMapping("/{weekPlanId}")
    public ResponseEntity<ApiResponse<Void>> deleteWeekPlan(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long weekPlanId
    ) {
        plannerService.deleteWeekPlan(weekPlanId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

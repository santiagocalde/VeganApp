package com.veganapp.streak.controller;

import com.veganapp.common.dto.ApiResponse;
import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import com.veganapp.streak.dto.CheckInRequest;
import com.veganapp.streak.dto.CheckInResponse;
import com.veganapp.streak.dto.StreakResponse;
import com.veganapp.streak.service.CheckInService;
import com.veganapp.streak.service.StreakService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para endpoints de rachas y check-in (ROUTER §9).
 * Responsabilidad única: validar requests HTTP y delegar lógica a servicios.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/streaks")
@RequiredArgsConstructor
public class StreakController {

    private final CheckInService checkInService;
    private final StreakService streakService;

    /**
     * POST /api/v1/streaks/checkin
     * Endpoint para registrar el check-in diario del usuario.
     *
     * Flujo exacto (ROUTER §11):
     * 1. Extrae userId del SecurityContext
     * 2. Valida el CheckInRequest
     * 3. Delega al CheckInService (ejecuta 11 pasos)
     * 4. Retorna ApiResponse<CheckInResponse> con resultados
     *
     * @param request CheckInRequest validado con @Valid
     * @return ResponseEntity<ApiResponse<CheckInResponse>>
     * @throws AppException si el usuario no existe, ya hizo check-in, etc.
     */
    @PostMapping("/checkin")
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(
            @Valid @RequestBody CheckInRequest request
    ) {
        // Extraer userId del SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Validar que existe autenticación
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized check-in attempt");
            throw new AppException(ErrorCode.ACCOUNT_DISABLED, "Usuario no autenticado");
        }

        Object principal = authentication.getPrincipal();
        
        // Pattern matching de Java 21: extraer userId de forma segura
        if (!(principal instanceof Long userId)) {
            log.error("Invalid principal type in SecurityContext: {}", principal.getClass());
            throw new AppException(ErrorCode.ACCESS_DENIED, "Contexto de autenticación inválido");
        }

        // Validar que userId es positivo (defensa en profundidad)
        if (userId <= 0) {
            log.error("Invalid userId: {}", userId);
            throw new AppException(ErrorCode.ACCESS_DENIED, "ID de usuario inválido");
        }

        try {
            log.debug("Check-in request from user={}, category={}", userId, request.category());

            // Delegar al servicio para ejecutar los 11 pasos del algoritmo
            CheckInResponse checkInResponse = checkInService.checkIn(userId, request);

            log.info("Check-in successful: user={}, newDays={}, pointsEarned={}", 
                    userId, checkInResponse.newDays(), checkInResponse.pointsEarned());

            return ResponseEntity.ok(ApiResponse.success(checkInResponse, "Check-in registrado correctamente"));
        } catch (AppException e) {
            // Re-lanzar para que GlobalExceptionHandler lo maneje
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during check-in for user={}: {}", userId, e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Error al procesar el check-in");
        }
    }

    /**
     * GET /api/v1/streaks/me
     *
     * Retorna información de racha del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StreakResponse>> getMyStreak(
            @AuthenticationPrincipal Long userId
    ) {
        StreakResponse response = streakService.getStreakInfoOrDefault(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
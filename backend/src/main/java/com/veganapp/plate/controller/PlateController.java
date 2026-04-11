package com.veganapp.plate.controller;

import com.veganapp.common.dto.ApiResponse;
import com.veganapp.plate.dto.PlateCalculationResult;
import com.veganapp.plate.dto.PlateOptionResponse;
import com.veganapp.plate.dto.PlateRequest;
import com.veganapp.plate.entity.PlateOption.PlateCategory;
import com.veganapp.plate.service.PlateBuilderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plates")
@RequiredArgsConstructor
public class PlateController {

    private final PlateBuilderService plateBuilderService;

    /**
     * GET /api/v1/plates/options?category=BASE
     *
     * Devuelve todas las opciones activas de una categoría
     */
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<List<PlateOptionResponse>>> getOptions(
            @RequestParam PlateCategory category
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(plateBuilderService.getOptionsByCategory(category))
        );
    }

    /**
     * POST /api/v1/plates/calculate
     *
     * Calcula un plato armado con sus macros y balance
     * Request: { baseId, proteinId, vegetableId, sauceId }
     * Response: { base, protein, vegetable, sauce, calories, protein, fat, isBalanced, message, pippinReaction }
     */
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<PlateCalculationResult>> calculatePlate(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PlateRequest request
    ) {
        PlateCalculationResult result = plateBuilderService.calculatePlate(request, userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}

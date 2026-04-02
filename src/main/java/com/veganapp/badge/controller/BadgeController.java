package com.veganapp.badge.controller;

import com.veganapp.badge.dto.BadgeResponse;
import com.veganapp.badge.service.BadgeService;
import com.veganapp.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    /**
     * GET /api/v1/badges
     *
     * Retorna todas las badges disponibles del sistema (catálogo)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BadgeResponse>>> getAllBadges() {
        return ResponseEntity.ok(
                ApiResponse.success(badgeService.getAllBadges())
        );
    }

    /**
     * GET /api/v1/badges/me
     *
     * Retorna todas las badges ganadas por el usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<BadgeResponse>>> getUserBadges(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(badgeService.getUserBadges(userId))
        );
    }
}

package com.veganapp.user.controller;

import com.veganapp.common.dto.ApiResponse;
import com.veganapp.user.dto.UserProfileResponse;
import com.veganapp.user.dto.UserResponseDTO;
import com.veganapp.user.dto.UserUpdateRequest;
import com.veganapp.user.entity.User.TonePref;
import com.veganapp.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/v1/users/me
     *
     * Retorna el perfil del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getProfile(userId))
        );
    }

    /**
     * PUT /api/v1/users/me
     *
     * Actualiza el perfil del usuario autenticado
     * Request: { name, email, tonePref, timezone, notifPush, notifEmail }
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.updateProfile(userId, request))
        );
    }

    /**
     * PUT /api/v1/users/me/tone
     *
     * Actualiza el tone de los mensajes de Pippin
     * Request: { tone: "MOTIVATIONAL" | "SARCASTIC" | "NEUTRAL" }
     */
    @PutMapping("/me/tone")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserTone(
            @AuthenticationPrincipal Long userId,
            @RequestParam TonePref tone
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.updateUserTone(userId, tone))
        );
    }

    /**
     * GET /api/v1/users/{id}
     *
     * Obtiene un usuario por ID (admin + auth)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getUserById(id))
        );
    }

    /**
     * GET /api/v1/users
     *
     * Retorna todos los usuarios (admin only)
     * TODO: Agregar @PreAuthorize("hasRole('ADMIN')")
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getAllUsers())
        );
    }
}

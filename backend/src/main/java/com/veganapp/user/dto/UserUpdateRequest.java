package com.veganapp.user.dto;

import com.veganapp.user.entity.User.TonePref;

/**
 * DTO para actualización de perfil del usuario
 * PUT /api/v1/users/me
 */
public record UserUpdateRequest(
        String name,
        String email,
        TonePref tonePref,
        String timezone,
        boolean notifPush,
        boolean notifEmail
) {
}

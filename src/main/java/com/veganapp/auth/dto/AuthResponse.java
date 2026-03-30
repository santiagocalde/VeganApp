package com.veganapp.auth.dto;

import com.veganapp.user.entity.User;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UserInfo user
) {
    public record UserInfo(
            Long id,
            String email,
            String name,
            User.ProfileType profileType,
            User.Motivation motivation,
            User.TonePref tonePref,
            User.ExperienceLevel experienceLevel,
            User.BudgetLevel budgetLevel,
            String timezone
    ) {}
}

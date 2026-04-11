package com.veganapp.user.dto;

import com.veganapp.user.entity.User;
import com.veganapp.user.entity.User.BudgetLevel;
import com.veganapp.user.entity.User.ExperienceLevel;
import com.veganapp.user.entity.User.Motivation;
import com.veganapp.user.entity.User.ProfileType;
import com.veganapp.user.entity.User.TonePref;

import java.time.Instant;

/**
 * DTO para respuestas de User en GET /api/v1/users/me, GET /api/v1/users/{id}
 */
public record UserResponseDTO(
        Long id,
        String email,
        String name,
        String avatar,
        ProfileType profileType,
        Motivation motivation,
        TonePref tonePref,
        ExperienceLevel experienceLevel,
        BudgetLevel budgetLevel,
        String timezone,
        boolean notifPush,
        boolean notifEmail,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                null, // Avatar URL no en la BD de User (será en futuro)
                user.getProfileType(),
                user.getMotivation(),
                user.getTonePref(),
                user.getExperienceLevel(),
                user.getBudgetLevel(),
                user.getTimezone(),
                user.isNotifPush(),
                user.isNotifEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

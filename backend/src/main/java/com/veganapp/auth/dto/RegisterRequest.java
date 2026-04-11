package com.veganapp.auth.dto;

import com.veganapp.user.entity.User;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "La contraseña debe tener al menos una mayúscula, una minúscula y un número"
        )
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String name,

        @NotNull(message = "El tipo de perfil es obligatorio")
        User.ProfileType profileType,

        @NotNull(message = "La motivación es obligatoria")
        User.Motivation motivation,

        @NotNull(message = "El nivel de experiencia es obligatorio")
        User.ExperienceLevel experienceLevel,

        @NotNull(message = "El nivel de presupuesto es obligatorio")
        User.BudgetLevel budgetLevel,

        @NotNull(message = "El tono de Sir Pippin es obligatorio")
        User.TonePref tonePref,

        @NotBlank(message = "El timezone es obligatorio")
        @Pattern(
                regexp = "^[A-Za-z]+(/[A-Za-z_]+)*$",
                message = "El timezone debe ser un identificador IANA válido (ej: America/Argentina/Buenos_Aires)"
        )
        String timezone
) {}

package com.veganapp.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth
    USER_NOT_FOUND("AUTH_001", "Usuario no encontrado", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("AUTH_002", "El email ya está registrado", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("AUTH_003", "Email o contraseña incorrectos", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("AUTH_004", "Token inválido o expirado", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("AUTH_005", "Refresh token no encontrado o expirado", HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED("AUTH_006", "La cuenta está desactivada", HttpStatus.FORBIDDEN),
    INVALID_RESET_TOKEN("AUTH_007", "Token de recuperación inválido o expirado", HttpStatus.BAD_REQUEST),

    // Streak
    ALREADY_CHECKED_IN("STREAK_001", "Ya realizaste el check-in de hoy", HttpStatus.CONFLICT),
    STREAK_NOT_FOUND("STREAK_002", "Racha no encontrada", HttpStatus.NOT_FOUND),
    MAX_PAUSES_REACHED("STREAK_003", "Alcanzaste el máximo de pausas del mes", HttpStatus.BAD_REQUEST),
    PAUSE_ALREADY_ACTIVE("STREAK_004", "Ya tenés una pausa activa", HttpStatus.CONFLICT),

    // Recipe
    RECIPE_NOT_FOUND("RECIPE_001", "Receta no encontrada", HttpStatus.NOT_FOUND),
    RECIPE_ALREADY_FAVORITED("RECIPE_002", "La receta ya está en favoritos", HttpStatus.CONFLICT),
    RECIPE_NOT_IN_FAVORITES("RECIPE_003", "La receta no está en favoritos", HttpStatus.NOT_FOUND),

    // Plate
    PLATE_OPTION_NOT_FOUND("PLATE_001", "Opción de plato no encontrada", HttpStatus.NOT_FOUND),

    // Planner
    PLANNER_ENTRY_NOT_FOUND("PLANNER_001", "Entrada del planificador no encontrada", HttpStatus.NOT_FOUND),

    // Shopping
    SHOPPING_LIST_NOT_FOUND("SHOPPING_001", "Lista de supermercado no encontrada", HttpStatus.NOT_FOUND),
    NO_ACTIVE_PLAN("SHOPPING_002", "No hay planificador activo para generar la lista", HttpStatus.BAD_REQUEST),

    // General
    VALIDATION_ERROR("GENERAL_001", "Error de validación", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("GENERAL_002", "Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED("GENERAL_003", "Acceso denegado", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND("GENERAL_004", "Recurso no encontrado", HttpStatus.NOT_FOUND);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

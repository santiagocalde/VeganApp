package com.veganapp.user.service;

import com.veganapp.user.dto.UserProfileResponse;
import com.veganapp.user.dto.UserUpdateRequest;
import com.veganapp.user.dto.UserResponseDTO;
import com.veganapp.user.entity.User.TonePref;

import java.util.List;

/**
 * Contrato del servicio de Usuario.
 *
 * Responsabilidades:
 * - CRUD básico de usuarios
 * - Actualizer perfil (nombre, email, avatar)
 * - Cambiar tone de mensajes (MOTIVATIONAL, SARCASTIC, NEUTRAL)
 * - Obtener perfil del usuario autenticado
 * - Listar usuarios (admin)
 */
public interface UserService {

    /**
     * Obtiene el perfil del usuario autenticado
     */
    UserProfileResponse getProfile(Long userId);

    /**
     * Actualiza los datos del perfil (nombre, email, avatar)
     */
    UserResponseDTO updateProfile(Long userId, UserUpdateRequest request);

    /**
     * Cambia el tone de los mensajes de Pippin
     */
    UserResponseDTO updateUserTone(Long userId, TonePref tone);

    /**
     * Retorna todos los usuarios (solo admin)
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Obtiene un usuario por ID (admin + auth)
     */
    UserResponseDTO getUserById(Long userId);
}

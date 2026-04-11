package com.veganapp.user.service;

import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import com.veganapp.user.dto.UserProfileResponse;
import com.veganapp.user.dto.UserResponseDTO;
import com.veganapp.user.dto.UserUpdateRequest;
import com.veganapp.user.entity.User;
import com.veganapp.user.entity.User.TonePref;
import com.veganapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de UserService.
 *
 * Responsabilidades:
 * - Obtener/actualizar perfil del usuario autenticado
 * - Cambiar tone de Pippin
 * - Listar usuarios (admin)
 * - Validación de permisos: Solo el usuario puede actualizar su propio perfil
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
        return UserProfileResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for update: {}", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        // Validar email único (si cambió)
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.email());
        }

        // Actualizar campos
        if (request.name() != null && !request.name().isEmpty()) {
            user.setName(request.name());
        }
        if (request.tonePref() != null) {
            user.setTonePref(request.tonePref());
        }
        if (request.timezone() != null && !request.timezone().isEmpty()) {
            user.setTimezone(request.timezone());
        }

        user.setNotifPush(request.notifPush());
        user.setNotifEmail(request.notifEmail());

        User saved = userRepository.save(user);
        log.info("User profile updated: {}", userId);
        return UserResponseDTO.from(saved);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserTone(Long userId, TonePref tone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for tone update: {}", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        if (tone != null) {
            user.setTonePref(tone);
            User saved = userRepository.save(user);
            log.info("User tone updated: userId={}, tone={}", userId, tone);
            return UserResponseDTO.from(saved);
        }

        return UserResponseDTO.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        // TODO: Agregar @PreAuthorize("hasRole('ADMIN')") en el controller
        return userRepository.findAll().stream()
                .map(UserResponseDTO::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
        return UserResponseDTO.from(user);
    }
}

package com.veganapp.notification.service;

import com.veganapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementación MVP del servicio de notificaciones.
 * En esta fase: loguea todas las notificaciones como INFO para verificar
 * que el CRON las dispara correctamente.
 *
 * TODO: Fase 2 — integrar Firebase Cloud Messaging:
 *   1. Agregar fcm_token a la tabla users
 *   2. Inyectar WebClient y llamar a FCM API
 *   3. Manejar 404 (token inválido) eliminándolo de la DB
 *   4. Agregar tabla de notificaciones para auditoría
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendStreakReminder(User user, String message) {
        log.info("[NOTIF-MVP] PUSH STREAK_REMINDER -> userId={}, email={}, message='{}'",
                user.getId(), user.getEmail(), message);
        // TODO: Fase 2 — FCM real
    }

    @Override
    public void sendStreakDanger(User user, String message, int daysAtRisk) {
        log.info("[NOTIF-MVP] PUSH STREAK_DANGER -> userId={}, email={}, daysAtRisk={}, message='{}'",
                user.getId(), user.getEmail(), daysAtRisk, message);
        // TODO: Fase 2 — FCM real con urgencia ALTA
    }

    @Override
    public void sendMilestoneUnlocked(User user, String badgeName, int days) {
        log.info("[NOTIF-MVP] PUSH MILESTONE -> userId={}, email={}, badge='{}', days={}",
                user.getId(), user.getEmail(), badgeName, days);
        // TODO: Fase 2 — FCM real con celebración
    }
}

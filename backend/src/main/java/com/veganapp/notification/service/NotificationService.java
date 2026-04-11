package com.veganapp.notification.service;

import com.veganapp.user.entity.User;

/**
 * Contrato del servicio de Notificaciones.
 *
 * En MVP (Fase 1):
 * - Loguea todas las notificaciones como INFO
 * - No envía realmente por FCM ni email
 *
 * En Fase 2:
 * - Integración real con Firebase Cloud Messaging
 * - Integración con Resend API para emails
 * - Manejo de fallback de tokens inválidos
 *
 * GRASP: Facade — abstrae la complejidad de múltiples canales de notificación
 */
public interface NotificationService {
    /**
     * Envía notificación push de recordatorio suave de check-in.
     *
     * Contexto: Usuario no hizo check-in hoy, pero su racha sigue vigente
     * Tono: Amigable, recordatorio sin urgencia
     */
    void sendStreakReminder(User user, String message);

    /**
     * Envía notificación push de peligro crítico de racha.
     *
     * Contexto: Usuario no hace check-in desde hace >24hs
     * Urgencia: ALTA - racha a punto de romperse
     * El mensaje debe incluir los días en juego
     */
    void sendStreakDanger(User user, String message, int daysAtRisk);

    /**
     * Envía notificación push de milestone/badge desbloqueado.
     *
     * Contexto: Usuario alcanzó un hito de racha (7, 30, 100 días)
     * Tono: Celebratorio
     */
    void sendMilestoneUnlocked(User user, String badgeName, int days);
}

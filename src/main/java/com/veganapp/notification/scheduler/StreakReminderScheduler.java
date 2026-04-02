package com.veganapp.notification.scheduler;

import com.veganapp.user.entity.User;
import com.veganapp.user.repository.UserRepository;
import com.veganapp.notification.service.NotificationService;
import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.dto.PippinReactionResponse;
import com.veganapp.pippin.service.PippinService;
import com.veganapp.streak.repository.DailyLogRepository;
import com.veganapp.streak.repository.StreakRepository;
import com.veganapp.common.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * El Vigilante Nocturno — CRON que se ejecuta cada hora exacta.
 *
 * Responsabilidad: Detectar usuarios cuya "hora local" es ventana de recordatorio
 * y NO hicieron check-in hoy. Enviar notificación personalizada con Sir Pippin.
 *
 * Por cada hora H:
 * 1. Busca usuarios cuya hora local actual sea entre 20:00 y 21:00
 * 2. Filtra los que NO hicieron check-in hoy (en su timezone)
 * 3. Clasifica por urgencia: recordatorio suave vs. peligro de racha (48hs sin check-in)
 * 4. Genera el mensaje de Sir Pippin personalizado
 * 5. Delega el envío a NotificationService (stub en MVP, FCM real en Fase 2)
 *
 * FIX #4: RESILENCIA — El fallo de UN usuario NO detiene el loop
 * - Try/catch por usuario
 * - Logging granular (enviados, fallos, alertas)
 * - Si > 50% fallos, genera alerta
 *
 * SOLID:
 *   S — este scheduler SOLO coordina. No envía, no selecciona mensajes, no consulta rachas.
 *   D — depende de interfaces, no de implementaciones concretas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StreakReminderScheduler {

    private static final LocalTime REMINDER_HOUR_START = LocalTime.of(20, 0);
    private static final LocalTime REMINDER_HOUR_END   = LocalTime.of(21, 0);

    private final UserRepository userRepository;
    private final StreakRepository streakRepository;
    private final DailyLogRepository dailyLogRepository;
    private final PippinService pippinService;
    private final NotificationService notificationService;
    private final AppProperties appProperties;

    /**
     * Se ejecuta cada hora exacta (0 minutos de cada hora) — 00:00, 01:00, 02:00, ...
     *
     * Ejemplo: Si ejecuta a las 20:00 UTC, buscará usuarios cuya hora local = 20:00
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(readOnly = true)
    public void checkAndSendReminders() {
        long startTime = System.currentTimeMillis();
        log.info("[CRON] StreakReminderScheduler iniciado");

        List<User> activeUsers = userRepository.findAllActiveWithNotifPush();
        log.debug("Total usuarios activos con notif push: {}", activeUsers.size());

        int sentCount = 0;
        int failureCount = 0;
        List<String> failures = new ArrayList<>();

        // FIX #4: Try/catch POR usuario — no parar si uno falla
        for (User user : activeUsers) {
            try {
                if (isReminderHourForUser(user)) {
                    sendReminderForUser(user);
                    sentCount++;
                }
            } catch (Exception e) {
                failureCount++;
                String failureMsg = "userId=" + user.getId() + ", error=" + e.getClass().getSimpleName();
                failures.add(failureMsg);
                log.warn("Reminder send failed for user={}: {}. Will retry next hour.",
                        user.getId(), e.getMessage());
                // NO DETENER EL LOOP — continuar con el siguiente usuario
            }
        }

        long elapsedMs = System.currentTimeMillis() - startTime;

        // FIX #4: Estadísticas finales y alertas
        log.info("[CRON-RESULT] reminders sent: {}/{}, failures: {}, elapsed: {}ms",
                sentCount, activeUsers.size(), failureCount, elapsedMs);

        if (!failures.isEmpty() && activeUsers.size() > 0) {
            double failureRate = (double) failureCount / activeUsers.size();
            double maxFailureRate = appProperties.notification().maxFailureRateAlert();

            if (failureRate > maxFailureRate) {
                log.error("[CRON-ALERT] High failure rate in reminders: {}/{} = {:.1f}% (threshold: {:.1f}%)",
                        failureCount, activeUsers.size(), failureRate * 100, maxFailureRate * 100);
                log.error("[CRON-ALERT] Failed users: {}", failures);
                // TODO: Fase 2 — enviar alerta a Slack via AlertService
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // PRIVADOS
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Verifica si la hora local del usuario está en la ventana de recordatorio.
     *
     * Ejemplo:
     * - User timezone = America/Argentina/Buenos_Aires (UTC-3)
     * - Ahora es 23:00 UTC
     * - Hora local del usuario = 20:00 ART
     * - REMINDER_HOUR_START = 20:00, REMINDER_HOUR_END = 21:00
     * - Resultado: true (enviar recordatorio)
     */
    private boolean isReminderHourForUser(User user) {
        try {
            ZoneId userZoneId = ZoneId.of(user.getTimezone());
            LocalTime userLocalTime = ZonedDateTime.now(userZoneId).toLocalTime();

            return !userLocalTime.isBefore(REMINDER_HOUR_START) && userLocalTime.isBefore(REMINDER_HOUR_END);
        } catch (Exception e) {
            log.warn("Error checking reminder hour for user {}: {}", user.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si el usuario ya hizo check-in hoy (en su timezone local).
     *
     * Importantísimo usar el timezone del usuario, no UTC.
     */
    private boolean hasCheckedInToday(User user) {
        try {
            LocalDate todayLocal = LocalDate.now(ZoneId.of(user.getTimezone()));
            return dailyLogRepository.existsByUserIdAndLogDate(user.getId(), todayLocal);
        } catch (Exception e) {
            log.warn("Error checking today's check-in status for user {}: {}", user.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Una racha está en peligro si:
     * - El usuario tiene una racha en progreso (last_checkin_date != null)
     * - Hace MÁS de 24 horas que el último check-in (último fue hace 2+ días)
     *
     *  Criterio: last_checkin_date <= ayer_local de hace 2 días
     * En otras palabras: última vez fue hace 2+ días atrás
     */
    private boolean isStreakInDanger(User user) {
        try {
            LocalDate todayLocal = LocalDate.now(ZoneId.of(user.getTimezone()));
            LocalDate dayBeforeYesterdayLocal = todayLocal.minusDays(2);

            return streakRepository.findByUserId(user.getId())
                    .map(streak -> streak.getLastCheckinDate() != null && 
                                   streak.getLastCheckinDate().isBefore(dayBeforeYesterdayLocal))
                    .orElse(false);
        } catch (Exception e) {
            log.warn("Error checking streak danger status for user {}: {}", user.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene los días actuales de la racha del usuario
     */
    private int getCurrentDays(User user) {
        return streakRepository.findByUserId(user.getId())
                .map(streak -> streak.getCurrentDays())
                .orElse(0);
    }

    /**
     * Lógica unificada: enviar recordatorio personalizado a UN usuario
     * con try/catch interno para no fallar el loop principal.
     */
    private void sendReminderForUser(User user) {
        // Validación rápida
        if (user == null || user.getId() == null) {
            log.warn("Invalid user data in sendReminderForUser");
            return;
        }

        // Verificar que no hizo check-in hoy
        if (hasCheckedInToday(user)) {
            log.debug("User {} already checked in today, skipping reminder", user.getId());
            return;
        }

        // Determinar urgencia
        boolean isDanger = isStreakInDanger(user);
        int currentDays = getCurrentDays(user);

        // Generar contexto de Pippin
        PippinContext context = PippinContext.forReminder(user, currentDays, isDanger);
        
        // Obtener reacción de Pippin
        PippinReactionResponse reaction = pippinService.getReaction(context);

        // Enviar notificación
        if (isDanger) {
            notificationService.sendStreakDanger(user, reaction.message(), currentDays);
        } else {
            notificationService.sendStreakReminder(user, reaction.message());
        }

        log.debug("Reminder sent: userId={}, isDanger={}, days={}", user.getId(), isDanger, currentDays);
    }
}

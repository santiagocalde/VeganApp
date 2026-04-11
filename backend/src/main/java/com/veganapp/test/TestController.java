package com.veganapp.test;

import com.veganapp.common.exception.ApiResponse;
import com.veganapp.notification.scheduler.StreakReminderScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController: Endpoints de prueba SOLO para perfiles dev y test.
 * NO exponer en producción.
 *
 * Propósito: Permitir que los tests de CI/CD disparen el CRON manualmente
 * sin esperar al horario configurado.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class TestController {

    private final StreakReminderScheduler scheduler;

    /**
     * POST /api/v1/test/trigger-cron
     * Dispara el CRON de recordatorios de racha manualmente (testing).
     * SOLO disponible en dev/test profiles.
     *
     * @return ApiResponse con mensaje de éxito
     */
    @PostMapping("/trigger-cron")
    public ResponseEntity<ApiResponse<String>> triggerCron() {
        log.info("Test endpoint: triggering CRON manually");
        try {
            scheduler.checkAndSendReminders();
            log.info("CRON ejecutado manualmente exitosamente");
            return ResponseEntity.ok(ApiResponse.success("CRON ejecutado manualmente"));
        } catch (Exception e) {
            log.error("Error al ejecutar CRON manualmente", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("CRON_ERROR", "Error al ejecutar CRON: " + e.getMessage()));
        }
    }
}

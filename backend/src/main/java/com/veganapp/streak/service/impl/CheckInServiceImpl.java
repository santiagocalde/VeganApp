package com.veganapp.streak.service.impl;

import com.veganapp.badge.service.BadgeService;
import com.veganapp.common.config.AppProperties;
import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.dto.PippinReactionResponse;
import com.veganapp.pippin.service.PippinService;
import com.veganapp.streak.dto.CheckInRequest;
import com.veganapp.streak.dto.CheckInResponse;
import com.veganapp.streak.dto.CheckInResponse.BadgeInfo;
import com.veganapp.streak.dto.CheckInResponse.PippinReaction;
import com.veganapp.streak.entity.DailyLog;
import com.veganapp.streak.entity.Streak;
import com.veganapp.streak.entity.StreakPause;
import com.veganapp.streak.repository.DailyLogRepository;
import com.veganapp.streak.repository.StreakPauseRepository;
import com.veganapp.streak.repository.StreakRepository;
import com.veganapp.streak.service.CheckInService;
import com.veganapp.user.entity.User;
import com.veganapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Implementación de CheckInService con el algoritmo exacto de 11 pasos (ROUTER §11).
 * Responsabilidad única: registrar el check-in diario del usuario manteniendo la integridad
 * de la racha, calculando puntos con multiplicador y emitiendo notificaciones de hitos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final UserRepository userRepository;
    private final StreakRepository streakRepository;
    private final DailyLogRepository dailyLogRepository;
    private final StreakPauseRepository streakPauseRepository;
    private final AppProperties appProperties;
    private final PippinService pippinService;
    private final BadgeService badgeService;

    /**
     * ALGORITMO DE CHECK-IN — 11 PASOS (ROUTER §11).
     * Implementa la lógica exacta sin desvíos.
     */
    @Override
    @Transactional
    public CheckInResponse checkIn(Long userId, CheckInRequest request) {

        // Validación: userId no puede ser nulo
        if (userId == null || userId <= 0) {
            log.error("Invalid userId: {}", userId);
            throw new AppException(ErrorCode.USER_NOT_FOUND, "ID de usuario inválido");
        }

        // Validación: request.category() no puede ser nulo
        if (request.category() == null) {
            log.error("Check-in category cannot be null");
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Categoría del check-in es requerida");
        }

        // ════════════════════════════════════════════════════════════════════════════
        // PASO 1: Obtener fecha LOCAL del usuario (timezone de users.timezone)
        // ════════════════════════════════════════════════════════════════════════════
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado: {}", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        // Validación: timezone debe estar configurado
        if (user.getTimezone() == null || user.getTimezone().trim().isEmpty()) {
            log.error("User timezone not configured for userId={}", userId);
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Timezone del usuario no está configurado");
        }

        ZoneId userZoneId;
        try {
            userZoneId = ZoneId.of(user.getTimezone());
        } catch (Exception e) {
            log.error("Invalid timezone for user={}: {}", userId, user.getTimezone(), e);
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Timezone inválido: " + user.getTimezone());
        }

        LocalDate todayLocal = ZonedDateTime.now(userZoneId).toLocalDate();
        LocalDate yesterdayLocal = todayLocal.minusDays(1);

        log.debug("Check-in attempt for user={}, timezone={}, todayLocal={}", userId, user.getTimezone(), todayLocal);

        // ════════════════════════════════════════════════════════════════════════════
        // PASO 2: Verificar duplicado (¿existe daily_log con log_date == hoy_local?)
        // ════════════════════════════════════════════════════════════════════════════
        boolean alreadyCheckedInToday = dailyLogRepository.findByUserIdAndLogDate(userId, todayLocal).isPresent();
        if (alreadyCheckedInToday) {
            log.warn("Duplicated check-in attempt for user={}, date={}", userId, todayLocal);
            throw new AppException(ErrorCode.ALREADY_CHECKED_IN, "Ya realizaste el check-in de hoy.");
        }

        // ════════════════════════════════════════════════════════════════════════════
        // PASO 3: Verificar pausa activa (¿existe streak_pause con expires_at > NOW()?)
        // ════════════════════════════════════════════════════════════════════════════
        Instant nowUtc = Instant.now();
        Optional<StreakPause> activePause = streakPauseRepository.findActiveByUserId(userId, nowUtc);
        boolean hasPauseActive = activePause.isPresent();

        if (hasPauseActive) {
            log.info("User {} has active pause, streak continuity not affected", userId);
        }

        // Justo antes de arrancar a calcular la continuidad
        boolean isComeback = false;


        // ════════════════════════════════════════════════════════════════════════════
        // PASO 4: Calcular continuidad de racha
        // ════════════════════════════════════════════════════════════════════════════
        Streak streak = streakRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Crear new streak si no existe
                    log.info("Creating new streak for user={}", userId);
                    return Streak.builder()
                            .userId(userId)
                            .currentDays(0)
                            .recordDays(0)
                            .totalPoints(0)
                            .level(Streak.StreakLevel.SEED)
                            .streakStartedAt(nowUtc)
                            .build();
                });

        int newDays = 1; // por defecto: primer check-in o racha rota
        boolean isComeback = false; // <--- 1. INICIAMOS LA BANDERA EN FALSO

        if (streak.getLastCheckinDate() != null) {
            if (streak.getLastCheckinDate().equals(yesterdayLocal)) {
                // Continuidad: check-in de ayer -> sumar día
                newDays = streak.getCurrentDays() + 1;
                log.debug("Consecutive check-in detected: {} + 1 = {}", streak.getCurrentDays(), newDays);
            } else {
                // Racha rota: último check-in no fue ayer (a menos que haya pausa)
                if (hasPauseActive) {
                    newDays = streak.getCurrentDays() + 1;
                    log.info("Check-in after gap but pause active: continuity maintained, days={}", newDays);
                } else {
                    // ¡PERDIÓ LA RACHA!
                    newDays = 1;
                    
                    // 2. LA MAGIA: Si ya tenía un récord mayor a 0, significa que volvió tras caer
                    if (streak.getRecordDays() > 0) {
                        isComeback = true; 
                    }
                    log.info("Streak broken: last check-in was {}, starting new streak. isComeback={}", streak.getLastCheckinDate(), isComeback);
                }
            }
        }

        // ════════════════════════════════════════════════════════════════════════════
        // PASO 5: Calcular multiplicador y puntos ganados
        // ════════════════════════════════════════════════════════════════════════════
        double multiplier = calculateMultiplier(newDays);
        int basePoints = appProperties.basePoints();
        
        // Validación de configuración
        if (basePoints <= 0) {
            log.error("Invalid basePoints configuration: {} (debe ser >= 1)", basePoints);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Configuración de puntos base inválida");
        }
        
        int pointsEarned = (int) (basePoints * multiplier);

        log.debug("Points calculation: basePoints={}, multiplier={}, pointsEarned={}", basePoints, multiplier, pointsEarned);

        // ════════════════════════════════════════════════════════════════════════════
        // PASO 6: @Transactional - guardar DailyLog + actualizar Streak (atomicidad)
        // ════════════════════════════════════════════════════════════════════════════
        DailyLog dailyLog = DailyLog.builder()
                .userId(userId)
                .logDate(todayLocal)
                .category(DailyLog.LogCategory.valueOf(request.category().name()))
                .notes(request.notes())
                .pointsEarned(pointsEarned)
                .build();
        dailyLogRepository.save(dailyLog);
        log.debug("DailyLog saved: userId={}, date={}, points={}", userId, todayLocal, pointsEarned);

        // Actualizar Streak
        long newTotalPoints = streak.getTotalPoints() + pointsEarned;
        streak.setCurrentDays(newDays);
        streak.setTotalPoints(newTotalPoints);
        streak.setLastCheckinDate(todayLocal);
        if (streak.getStreakStartedAt() == null) {
            streak.setStreakStartedAt(nowUtc);
        }

        // ════════════════════════════════════════════════════════════════════════════
        // PASO 7: Actualizar record_days si current_days > record_days
        // ════════════════════════════════════════════════════════════════════════════
        if (newDays > streak.getRecordDays()) {
            streak.setRecordDays(newDays);
            log.info("New record! User={} achieved {} consecutive days", userId, newDays);
        }

        // ════════════════════════════════════════════════════════════════════════════
        // PASO 8: Calcular nivel por total_points
        // ════════════════════════════════════════════════════════════════════════════
        Streak.StreakLevel newLevel = calculateLevel(newTotalPoints);
        streak.setLevel(newLevel);
        log.debug("Level calculation: totalPoints={}, newLevel={}", newTotalPoints, newLevel);

        // Persist streaks (dentro de @Transactional)
        streakRepository.save(streak);
        log.debug("Streak updated: userId={}, currentDays={}, totalPoints={}, level={}", userId, newDays, newTotalPoints, newLevel);

        // ════════════════════════════════════════════════════════════════════════════
        // PASO 9: Verificar hito de badge (3, 7, 14, 30, 60, 100, 180, 365 días)
        // ════════════════════════════════════════════════════════════════════════════
        BadgeInfo badgeInfo = null;
        int[] milestones = {3, 7, 14, 30, 60, 100, 180, 365};
        for (int milestone : milestones) {
            if (newDays == milestone) {
                log.info("MILESTONE ACHIEVED: User={} reached {} days!", userId, milestone);
                
                // BadgeService verifica y otorga badge (asíncrono con @Async)
                // No bloquea el response del check-in
                badgeService.checkAndAwardStreakBadge(userId, newDays);
                
                badgeInfo = new BadgeInfo(
                        "STREAK_" + milestone,
                        "¡" + milestone + " días en racha!",
                        "Hito alcanzado: " + milestone + " días consecutivos"
                );
                break;
            }
        }

        // ════════════════════════════════════════════════════════════════════════════
        // PASO 10: Obtener reacción de Sir Pippin
        // ════════════════════════════════════════════════════════════════════════════
        PippinContext pippinContext;
        if (isComeback) {
            // Usuario volvió después de romper racha
            pippinContext = PippinContext.forComeback(user);
        } else {
            // Check-in normal o hito — PippinContext.forCheckin detecta automáticamente
            pippinContext = PippinContext.forCheckin(user, newDays);
        }
        
        PippinReactionResponse pippinReaction = pippinService.getReaction(pippinContext);
        String animationState = pippinReaction.animationState();
        String pippinMessage = pippinReaction.message();

        // ════════════════════════════════════════════════════════════════════════════
        // PASO 11: Devolver CheckInResponse
        // ════════════════════════════════════════════════════════════════════════════
        CheckInResponse response = new CheckInResponse(
                newDays,
                pointsEarned,
                newLevel,
                badgeInfo,
                new PippinReaction(pippinMessage, animationState)
        );

        log.info("Check-in completed successfully: userId={}, newDays={}, pointsEarned={}, level={}", userId, newDays, pointsEarned, newLevel);
        return response;
    }

    /**
     * Calcula el multiplicador de puntos según los días acumulados (ROUTER §11 Paso 5).
     * <ul>
     *   <li>current_days >= 100 -> x3.0</li>
     *   <li>current_days >= 30  -> x2.0</li>
     *   <li>current_days >= 7   -> x1.5</li>
     *   <li>default             -> x1.0</li>
     * </ul>
     */
    private double calculateMultiplier(int currentDays) {
        if (currentDays >= 100) return 3.0;
        if (currentDays >= 30) return 2.0;
        if (currentDays >= 7) return 1.5;
        return 1.0;
    }

    /**
     * Calcula el nivel actual según puntos totales (ROUTER §11 Paso 8).
     * <ul>
     *   <li>SEED:   0 - 99</li>
     *   <li>SPROUT: 100 - 499</li>
     *   <li>PLANT:  500 - 1999</li>
     *   <li>TREE:   2000 - 9999</li>
     *   <li>FOREST: 10000+</li>
     * </ul>
     */
    private Streak.StreakLevel calculateLevel(long totalPoints) {
        if (totalPoints >= 10000) return Streak.StreakLevel.FOREST;
        if (totalPoints >= 2000) return Streak.StreakLevel.TREE;
        if (totalPoints >= 500) return Streak.StreakLevel.PLANT;
        if (totalPoints >= 100) return Streak.StreakLevel.SPROUT;
        return Streak.StreakLevel.SEED;
    }

    /**
     * Obtiene la reacción de Sir Pippin personalizada según contexto (ROUTER §11 Paso 10).
     * Temporalmente hardcodeado; en Fase 2 se integrará PippinMessageService
     * que lee desde resources/pippin/messages_es.json (ROUTER §12).
     */
    private PippinReaction getPippinReaction(User user, int newDays, boolean badgeUnlocked) {
        String message;
        String animationState;

        if (badgeUnlocked) {
            animationState = "celebration";
            message = "¡Lo estás rompiendo! Alcanzaste un hito. Sir Pippin está muy orgulloso.";
        } else if (newDays == 1) {
            animationState = "surprised";
            message = switch (user.getTonePref()) {
                case MOTIVATIONAL -> "¡Qué bien! El primer día siempre es importante. Vamos con todo.";
                case SARCASTIC -> "Mirá vos, apareciste. Bienvenido de vuelta.";
                case NEUTRAL -> "Check-in registrado. Empezaste una nueva racha.";
            };
        } else {
            animationState = "idle";
            message = switch (user.getTonePref()) {
                case MOTIVATIONAL -> "¡Excelente! Llevas " + newDays + " días seguidos. Estás en la zona.";
                case SARCASTIC -> "Mirá vos, " + newDays + " días seguidos. Cada vez más constante.";
                case NEUTRAL -> "Check-in registrado. Llevas " + newDays + " días consecutivos.";
            };
        }

        return new PippinReaction(message, animationState, "checkin");
    }
}

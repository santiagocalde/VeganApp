package com.veganapp.streak.service;

import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import com.veganapp.streak.dto.StreakResponse;
import com.veganapp.streak.entity.Streak;
import com.veganapp.streak.repository.StreakRepository;
import com.veganapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Implementación de StreakService.
 *
 * Responsabilidades:
 * - Calcular estado de racha
 * - Determinar si está en peligro (> 48h sin check-in)
 * - Obtener datos para dashboard
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreakServiceImpl implements StreakService {

    private final StreakRepository streakRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<StreakResponse> getStreakInfo(Long userId) {
        return streakRepository.findByUserId(userId)
                .map(this::buildStreakResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StreakResponse getStreakInfoOrDefault(Long userId) {
        // Validar que el usuario existe
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        return streakRepository.findByUserId(userId)
                .map(this::buildStreakResponse)
                .orElse(new StreakResponse(
                        userId,
                        0,      // currentDays
                        0,      // bestDays
                        null,   // lastCheckIn
                        null,   // bestStreakStartDate
                        null,   // bestStreakEndDate
                        false,  // inDanger
                        0       // hoursToBreak
                ));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // PRIVADOS
    // ────────────────────────────────────────────────────────────────────────────

    private StreakResponse buildStreakResponse(Streak streak) {
        LocalDate today = LocalDate.now();
        LocalDate lastCheckIn = streak.getLastCheckinDate();

        // Calcular si está en peligro (> 48h sin check-in)
        boolean inDanger = false;
        int hoursToBreak = 0;

        if (lastCheckIn != null) {
            long hoursSinceCheckIn = ChronoUnit.HOURS.between(
                    lastCheckIn.atStartOfDay(),
                    today.atStartOfDay()
            );

            inDanger = hoursSinceCheckIn > 48;
            hoursToBreak = Math.max(0, (int) (72 - hoursSinceCheckIn)); // 72 horas = 3 días
        }

        // Calcular fechas de mejor racha (aproximado)
        // Asumimos que la mejor racha terminó hace X días
        LocalDate bestStreakEndDate = null;
        LocalDate bestStreakStartDate = null;
        
        if (streak.getRecordDays() > 0) {
            // Si la mejor racha es la actual
            if (streak.getRecordDays() == streak.getCurrentDays()) {
                if (streak.getStreakStartedAt() != null) {
                    bestStreakStartDate = streak.getStreakStartedAt()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                }
                bestStreakEndDate = today;
            } else {
                // Si no, la mejor racha fue en el pasado
                // Estimación: terminó antes de hoy
                bestStreakEndDate = today.minusDays(1);
                bestStreakStartDate = bestStreakEndDate.minusDays(streak.getRecordDays() - 1);
            }
        }

        return new StreakResponse(
                streak.getUserId(),
                streak.getCurrentDays(),
                streak.getRecordDays(),
                lastCheckIn,
                bestStreakStartDate,
                bestStreakEndDate,
                inDanger,
                hoursToBreak
        );
    }
}

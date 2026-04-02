package com.veganapp.planner.service;

import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import com.veganapp.planner.dto.WeekPlanCreateRequest;
import com.veganapp.planner.dto.WeekPlanEntryCreateRequest;
import com.veganapp.planner.dto.WeekPlanEntryResponse;
import com.veganapp.planner.dto.WeekPlanResponse;
import com.veganapp.planner.entity.WeekPlan;
import com.veganapp.planner.entity.WeekPlanEntry;
import com.veganapp.planner.repository.WeekPlanEntryRepository;
import com.veganapp.planner.repository.WeekPlanRepository;
import com.veganapp.recipe.entity.Recipe;
import com.veganapp.recipe.repository.RecipeRepository;
import com.veganapp.user.entity.User;
import com.veganapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación de PlannerService.
 *
 * Responsabilidades:
 * - Gestionar planes semanales
 * - Crear entries con validación de recetas
 * - Marcar planes como completados
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlannerServiceImpl implements PlannerService {

    private final WeekPlanRepository weekPlanRepository;
    private final WeekPlanEntryRepository weekPlanEntryRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Override
    @Transactional
    public WeekPlanResponse getOrCreateWeekPlan(Long userId, LocalDate weekStartDate) {
        // Validar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        // Buscar plan existente
        Optional<WeekPlan> existing = weekPlanRepository.findByUserIdAndWeekStartDate(userId, weekStartDate);
        if (existing.isPresent()) {
            return buildWeekPlanResponse(existing.get());
        }

        // Crear nuevo plan vacío
        WeekPlan newPlan = WeekPlan.builder()
                .user(user)
                .weekStartDate(weekStartDate)
                .completed(false)
                .build();

        WeekPlan saved = weekPlanRepository.save(newPlan);
        log.info("WeekPlan created: userId={}, weekStartDate={}", userId, weekStartDate);

        return buildWeekPlanResponse(saved);
    }

    @Override
    @Transactional
    public WeekPlanResponse createWeekPlan(Long userId, WeekPlanCreateRequest request) {
        // Validar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        // Crear plan
        WeekPlan plan = WeekPlan.builder()
                .user(user)
                .weekStartDate(request.weekStartDate())
                .completed(false)
                .build();

        WeekPlan saved = weekPlanRepository.save(plan);

        // Crear entries
        if (request.entries() != null && !request.entries().isEmpty()) {
            for (WeekPlanEntryCreateRequest entryRequest : request.entries()) {
                addEntryToPlan(saved.getId(), entryRequest, userId);
            }
        }

        log.info("WeekPlan created with entries: userId={}, weekStartDate={}", userId, request.weekStartDate());

        return buildWeekPlanResponse(weekPlanRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WeekPlanResponse> getWeekPlan(Long weekPlanId, Long userId) {
        return weekPlanRepository.findById(weekPlanId)
                .filter(wp -> wp.getUser().getId().equals(userId))
                .map(this::buildWeekPlanResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WeekPlanResponse> getLatestWeekPlan(Long userId) {
        return weekPlanRepository.findLatestByUserId(userId)
                .map(this::buildWeekPlanResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeekPlanResponse> getAllWeekPlans(Long userId) {
        return weekPlanRepository.findByUserId(userId).stream()
                .map(this::buildWeekPlanResponse)
                .toList();
    }

    @Override
    @Transactional
    public WeekPlanResponse markAsCompleted(Long weekPlanId, Long userId) {
        WeekPlan plan = weekPlanRepository.findById(weekPlanId)
                .filter(wp -> wp.getUser().getId().equals(userId))
                .orElseThrow(() -> {
                    log.warn("WeekPlan not found or not owned by user: {}", weekPlanId);
                    return new AppException(ErrorCode.INTERNAL_ERROR, "Plan no encontrado");
                });

        plan.setCompleted(true);
        plan.setCompletedAt(Instant.now());

        WeekPlan saved = weekPlanRepository.save(plan);
        log.info("WeekPlan marked as completed: {}", weekPlanId);

        return buildWeekPlanResponse(saved);
    }

    @Override
    @Transactional
    public void deleteWeekPlan(Long weekPlanId, Long userId) {
        WeekPlan plan = weekPlanRepository.findById(weekPlanId)
                .filter(wp -> wp.getUser().getId().equals(userId))
                .orElseThrow(() -> {
                    log.warn("WeekPlan not found or not owned by user: {}", weekPlanId);
                    return new AppException(ErrorCode.INTERNAL_ERROR, "Plan no encontrado");
                });

        weekPlanRepository.delete(plan);
        log.info("WeekPlan deleted: {}", weekPlanId);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // PRIVADOS
    // ────────────────────────────────────────────────────────────────────────────

    private void addEntryToPlan(Long weekPlanId, WeekPlanEntryCreateRequest entryRequest, Long userId) {
        WeekPlan plan = weekPlanRepository.findById(weekPlanId).orElseThrow();

        Recipe recipe = recipeRepository.findById(entryRequest.recipeId())
                .orElseThrow(() -> {
                    log.warn("Recipe not found: {}", entryRequest.recipeId());
                    return new AppException(ErrorCode.INTERNAL_ERROR, "Receta no encontrada");
                });

        WeekPlanEntry entry = WeekPlanEntry.builder()
                .weekPlan(plan)
                .dayOfWeek(entryRequest.dayOfWeek())
                .mealType(entryRequest.mealType())
                .recipe(recipe)
                .servingsOverride(entryRequest.servingsOverride())
                .build();

        weekPlanEntryRepository.save(entry);
    }

    private WeekPlanResponse buildWeekPlanResponse(WeekPlan weekPlan) {
        Set<WeekPlanEntryResponse> entries = weekPlanEntryRepository
                .findByWeekPlanId(weekPlan.getId()).stream()
                .map(WeekPlanEntryResponse::from)
                .collect(Collectors.toSet());

        return WeekPlanResponse.from(weekPlan, entries);
    }
}

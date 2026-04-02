package com.veganapp.badge.repository;

import com.veganapp.badge.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    /**
     * Obtiene todos los badges ganados por un usuario, ordenados por fecha
     */
    List<UserBadge> findByUserIdOrderByEarnedAtDesc(Long userId);

    /**
     * Verifica si un usuario ya ganó un badge específico (idempotencia)
     */
    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);

    /**
     * Obtiene los IDs de badges ganados por un usuario (para ágilidad)
     */
    @Query("SELECT ub.badge.id FROM UserBadge ub WHERE ub.user.id = :userId")
    Set<Long> findBadgeIdsByUserId(Long userId);

    /**
     * Obtiene la fecha en que un usuario ganó un badge específico
     */
    @Query("SELECT ub.earnedAt FROM UserBadge ub WHERE ub.user.id = :userId AND ub.badge.id = :badgeId")
    Optional<Instant> findEarnedAtByUserIdAndBadgeId(Long userId, Long badgeId);
}

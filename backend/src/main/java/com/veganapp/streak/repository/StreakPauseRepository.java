package com.veganapp.streak.repository;

import com.veganapp.streak.entity.StreakPause;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface StreakPauseRepository extends JpaRepository<StreakPause, Long> {

    /**
     * Busca una pausa activa para el usuario.
     * Una pausa está activa si expires_at > NOW() (ahora en UTC).
     *
     * @param userId    ID del usuario
     * @param nowUtc    Tiempo actual en UTC
     * @return La primera pausa activa encontrada, si existe
     */
    @Query("SELECT sp FROM StreakPause sp WHERE sp.userId = :userId AND sp.expiresAt > :nowUtc LIMIT 1")
    Optional<StreakPause> findActiveByUserId(@Param("userId") Long userId, @Param("nowUtc") Instant nowUtc);
}

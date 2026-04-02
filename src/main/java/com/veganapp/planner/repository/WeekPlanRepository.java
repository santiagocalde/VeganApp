package com.veganapp.planner.repository;

import com.veganapp.planner.entity.WeekPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeekPlanRepository extends JpaRepository<WeekPlan, Long> {

    /**
     * Busca el plan semanal de un usuario para una fecha específica
     */
    @Query("""
            SELECT wp FROM WeekPlan wp 
            WHERE wp.user.id = :userId 
            AND wp.weekStartDate = :weekStartDate
            """)
    Optional<WeekPlan> findByUserIdAndWeekStartDate(
            @Param("userId") Long userId,
            @Param("weekStartDate") LocalDate weekStartDate
    );

    /**
     * Busca todos los planes de un usuario
     */
    @Query("SELECT wp FROM WeekPlan wp WHERE wp.user.id = :userId ORDER BY wp.weekStartDate DESC")
    List<WeekPlan> findByUserId(@Param("userId") Long userId);

    /**
     * Busca el plan semanal más reciente de un usuario
     */
    @Query("""
            SELECT wp FROM WeekPlan wp 
            WHERE wp.user.id = :userId 
            ORDER BY wp.weekStartDate DESC 
            LIMIT 1
            """)
    Optional<WeekPlan> findLatestByUserId(@Param("userId") Long userId);
}

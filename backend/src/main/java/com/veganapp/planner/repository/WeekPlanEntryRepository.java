package com.veganapp.planner.repository;

import com.veganapp.planner.entity.WeekPlanEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeekPlanEntryRepository extends JpaRepository<WeekPlanEntry, Long> {

    /**
     * Busca todos los entries de un plan semanal
     */
    @Query("SELECT wpe FROM WeekPlanEntry wpe WHERE wpe.weekPlan.id = :weekPlanId ORDER BY wpe.dayOfWeek, wpe.mealType")
    List<WeekPlanEntry> findByWeekPlanId(@Param("weekPlanId") Long weekPlanId);

    /**
     * Busca los entries de un usuario y semana específica
     */
    @Query("""
            SELECT wpe FROM WeekPlanEntry wpe 
            WHERE wpe.weekPlan.user.id = :userId 
            AND wpe.weekPlan.weekStartDate = :weekStartDate
            ORDER BY wpe.dayOfWeek, wpe.mealType
            """)
    List<WeekPlanEntry> findByUserIdAndWeekStartDate(
            @Param("userId") Long userId,
            @Param("weekStartDate") java.time.LocalDate weekStartDate
    );
}

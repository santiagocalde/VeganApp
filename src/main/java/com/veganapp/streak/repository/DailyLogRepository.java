package com.veganapp.streak.repository;

import com.veganapp.streak.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    Optional<DailyLog> findByUserIdAndLogDate(Long userId, java.time.LocalDate logDate);

    List<DailyLog> findByUserIdOrderByLogDateDesc(Long userId);
}

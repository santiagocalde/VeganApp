package com.veganapp.streak.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "streaks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Streak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "current_days", nullable = false)
    private int currentDays;

    @Column(name = "record_days", nullable = false)
    private int recordDays;

    @Column(name = "total_points", nullable = false)
    private long totalPoints;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StreakLevel level = StreakLevel.SEED;

    @Column(name = "last_checkin_date")
    private LocalDate lastCheckinDate;

    @Column(name = "streak_started_at")
    private Instant streakStartedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum StreakLevel {
        SEED, SPROUT, PLANT, TREE, FOREST
    }
}

package com.veganapp.streak.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "daily_logs",
        uniqueConstraints = @UniqueConstraint(name = "uq_daily_log_user_date", columnNames = { "user_id", "log_date" }),
        indexes = @Index(name = "idx_daily_logs_user_date", columnList = "user_id, log_date")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LogCategory category = LogCategory.FOOD;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "points_earned", nullable = false)
    @Builder.Default
    private int pointsEarned = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public enum LogCategory {
        FOOD, RECIPE, REFLECTION, ACTIVITY
    }
}

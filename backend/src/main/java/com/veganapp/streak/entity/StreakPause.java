package com.veganapp.streak.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "streak_pauses",
        indexes = @Index(name = "idx_streak_pauses_user_month", columnList = "user_id, month_year")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreakPause {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private Instant appliedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "month_year", nullable = false, length = 7)
    private String monthYear;
}

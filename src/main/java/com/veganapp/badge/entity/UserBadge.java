package com.veganapp.badge.entity;

import com.veganapp.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Entity: Badges ganados por usuarios.
 *
 * Relación many-to-many con constraint único:
 * Un usuario puede ganar cada badge una sola vez.
 *
 * earnedAt: timestamp de cuándo se ganó (útil para ordenar, analytics, etc.)
 */
@Entity
@Table(
    name = "user_badges",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "badge_id"}),
    indexes = {
        @Index(name = "idx_user_badges_user", columnList = "user_id"),
        @Index(name = "idx_user_badges_earned", columnList = "earned_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @CreationTimestamp
    @Column(name = "earned_at", nullable = false, updatable = false)
    private Instant earnedAt;
}

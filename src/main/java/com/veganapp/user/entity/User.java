package com.veganapp.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_type", nullable = false)
    private ProfileType profileType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Motivation motivation;

    @Enumerated(EnumType.STRING)
    @Column(name = "tone_pref", nullable = false)
    @Builder.Default
    private TonePref tonePref = TonePref.MOTIVATIONAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false)
    @Builder.Default
    private ExperienceLevel experienceLevel = ExperienceLevel.BEGINNER;

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_level", nullable = false)
    @Builder.Default
    private BudgetLevel budgetLevel = BudgetLevel.NORMAL;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String timezone = "UTC";

    @Column(name = "notif_push", nullable = false)
    @Builder.Default
    private boolean notifPush = true;

    @Column(name = "notif_email", nullable = false)
    @Builder.Default
    private boolean notifEmail = true;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum ProfileType { VEGAN, VEGETARIAN, FLEXITARIAN, TRANSITIONING }
    public enum Motivation { HEALTH, ETHICS, FITNESS, CURIOSITY }
    public enum TonePref { MOTIVATIONAL, SARCASTIC, NEUTRAL }
    public enum ExperienceLevel { BEGINNER, EXPERIENCED }
    public enum BudgetLevel { LOW, NORMAL, HIGH }

    // Getters explícitos (Lombok @Getter no se procesa correctamente en el entorno)
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public Motivation getMotivation() {
        return motivation;
    }

    public TonePref getTonePref() {
        return tonePref;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public BudgetLevel getBudgetLevel() {
        return budgetLevel;
    }

    public String getTimezone() {
        return timezone;
    }

    public boolean isNotifPush() {
        return notifPush;
    }

    public boolean isNotifEmail() {
        return notifEmail;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // Setters explícitos
    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public void setMotivation(Motivation motivation) {
        this.motivation = motivation;
    }

    public void setTonePref(TonePref tonePref) {
        this.tonePref = tonePref;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public void setBudgetLevel(BudgetLevel budgetLevel) {
        this.budgetLevel = budgetLevel;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setNotifPush(boolean notifPush) {
        this.notifPush = notifPush;
    }

    public void setNotifEmail(boolean notifEmail) {
        this.notifEmail = notifEmail;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package com.veganapp.pippin.domain;

import com.veganapp.user.entity.User;

/**
 * Value Object que encapsula todo el contexto necesario para que Sir Pippin
 * seleccione el mensaje correcto. Immutable por diseño (record Java).
 *
 * GRASP Information Expert: este objeto tiene la información
 * necesaria para tomar la decisión de selección. Lo pasa quien tiene
 * los datos (CheckInService, StreakReminderScheduler, etc.)
 */
public record PippinContext(
        PippinMessageContext messageContext,
        User.TonePref tone,
        User.Motivation motivation,
        int currentDays,
        Long userId
) {

    /**
     * Enum de contextos disponibles. Cada valor mapea a una MessageStrategy.
     * Para agregar un nuevo contexto: agregar el enum + crear la Strategy.
     * No tocar código existente. (Principio Open/Closed)
     */
    public enum PippinMessageContext {
        CHECKIN,
        MILESTONE_3,
        MILESTONE_7,
        MILESTONE_14,
        MILESTONE_30,
        MILESTONE_60,
        MILESTONE_100,
        MILESTONE_180,
        MILESTONE_365,
        COMEBACK,      // regresa después de perder racha
        REMINDER,      // no hizo check-in, aviso suave
        STREAK_DANGER, // 48hs sin check-in, aviso urgente
        PLATE_BUILT,   // armó su plato
        PLAN_COMPLETED // completó el planificador semanal
    }

    /** Factory method semántico para el caso más común */
    public static PippinContext forCheckin(User user, int currentDays) {
        PippinMessageContext ctx = resolveMilestoneContext(currentDays);
        return new PippinContext(ctx, user.getTonePref(), user.getMotivation(), currentDays, user.getId());
    }

    public static PippinContext forReminder(User user, int currentDays, boolean isDanger) {
        PippinMessageContext ctx = isDanger
                ? PippinMessageContext.STREAK_DANGER
                : PippinMessageContext.REMINDER;
        return new PippinContext(ctx, user.getTonePref(), user.getMotivation(), currentDays, user.getId());
    }

    public static PippinContext forComeback(User user) {
        return new PippinContext(
                PippinMessageContext.COMEBACK,
                user.getTonePref(), user.getMotivation(), 1, user.getId()
        );
    }

    public static PippinContext forPlateBuilt(User user) {
        return new PippinContext(
                PippinMessageContext.PLATE_BUILT,
                user.getTonePref(), user.getMotivation(), 0, user.getId()
        );
    }

    public static PippinContext forPlanCompleted(User user) {
        return new PippinContext(
                PippinMessageContext.PLAN_COMPLETED,
                user.getTonePref(), user.getMotivation(), 0, user.getId()
        );
    }

    /** Determina si es un check-in normal o un hito especial */
    private static PippinMessageContext resolveMilestoneContext(int days) {
        return switch (days) {
            case 3   -> PippinMessageContext.MILESTONE_3;
            case 7   -> PippinMessageContext.MILESTONE_7;
            case 14  -> PippinMessageContext.MILESTONE_14;
            case 30  -> PippinMessageContext.MILESTONE_30;
            case 60  -> PippinMessageContext.MILESTONE_60;
            case 100 -> PippinMessageContext.MILESTONE_100;
            case 180 -> PippinMessageContext.MILESTONE_180;
            case 365 -> PippinMessageContext.MILESTONE_365;
            default  -> PippinMessageContext.CHECKIN;
        };
    }
}

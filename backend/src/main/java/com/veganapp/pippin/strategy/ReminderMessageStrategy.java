package com.veganapp.pippin.strategy;

import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.domain.PippinContext.PippinMessageContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Strategy para recordatorios. Maneja dos niveles de urgencia:
 * - REMINDER: recordatorio suave (no hizo check-in hoy)
 * - STREAK_DANGER: urgente (lleva más de 24hs sin check-in, racha en peligro)
 *
 * El mensaje STREAK_DANGER incluye los días en juego para crear urgencia emocional.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderMessageStrategy implements MessageStrategy {

    private final PippinMessageLoader messageLoader;

    @Override
    public PippinMessageContext handles() {
        return PippinMessageContext.REMINDER;
    }

    public PippinMessageContext handlesAlso() {
        return PippinMessageContext.STREAK_DANGER;
    }

    @Override
    public List<String> getMessagePool(PippinContext context) {
        String messageKey = context.messageContext() == PippinMessageContext.STREAK_DANGER
                ? "STREAK_DANGER"
                : "REMINDER";
        
        List<String> pool = messageLoader.getMessages(messageKey, context.tone());
        
        // Si es STREAK_DANGER, interpolar días
        if (context.messageContext() == PippinMessageContext.STREAK_DANGER) {
            return pool.stream()
                    .map(msg -> String.format(msg, context.currentDays()))
                    .toList();
        }
        
        return pool;
    }
}

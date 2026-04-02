package com.veganapp.pippin.strategy;

import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.domain.PippinContext.PippinMessageContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Strategy para hitos especiales de racha (3, 7, 14, 30, 60, 100, 180, 365 días).
 * Una sola clase maneja todos los hitos para evitar duplicación.
 * Los mensajes son más dramáticos y celebratorios que los de check-in normal.
 *
 * Nota: esta clase registra multiples contextos en MessageStrategyRegistry.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MilestoneMessageStrategy implements MessageStrategy {

    private static final Set<PippinMessageContext> MILESTONE_CONTEXTS = Set.of(
        PippinMessageContext.MILESTONE_3,
        PippinMessageContext.MILESTONE_7,
        PippinMessageContext.MILESTONE_14,
        PippinMessageContext.MILESTONE_30,
        PippinMessageContext.MILESTONE_60,
        PippinMessageContext.MILESTONE_100,
        PippinMessageContext.MILESTONE_180,
        PippinMessageContext.MILESTONE_365
    );

    private final PippinMessageLoader messageLoader;

    @Override
    public PippinMessageContext handles() {
        // Este Strategy maneja múltiples contextos.
        // El registry lo registra para cada uno.
        // Devolvemos MILESTONE_7 como representativo pero el registry lo registra para todos.
        return PippinMessageContext.MILESTONE_7;
    }

    public Set<PippinMessageContext> handlesAll() {
        return MILESTONE_CONTEXTS;
    }

    @Override
    public List<String> getMessagePool(PippinContext context) {
        // Mapear contexto del enum al nombre de clave en JSON
        String messageKey = contextToKey(context.messageContext());
        
        return messageLoader.getMessages(messageKey, context.tone());
    }

    /** Convierte PippinMessageContext a la clave JSON (MILESTONE_7 -> MILESTONE_7) */
    private String contextToKey(PippinMessageContext ctx) {
        return ctx.name();
    }
}

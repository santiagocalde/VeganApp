package com.veganapp.pippin.strategy;

import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.domain.PippinContext.PippinMessageContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Strategy para cuando el usuario completa el planificador semanal.
 * Los mensajes felicitan por la organización.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlanCompletedMessageStrategy implements MessageStrategy {

    private final PippinMessageLoader messageLoader;

    @Override
    public PippinMessageContext handles() {
        return PippinMessageContext.PLAN_COMPLETED;
    }

    @Override
    public List<String> getMessagePool(PippinContext context) {
        return messageLoader.getMessages("PLAN_COMPLETED", context.tone());
    }
}

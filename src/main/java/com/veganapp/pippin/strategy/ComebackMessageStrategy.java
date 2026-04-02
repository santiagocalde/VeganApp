package com.veganapp.pippin.strategy;

import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.domain.PippinContext.PippinMessageContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Strategy para cuando el usuario regresa después de haber perdido su racha.
 * El tono es siempre de bienvenida (incluso en SARCASTIC, con humor suave).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComebackMessageStrategy implements MessageStrategy {

    private final PippinMessageLoader messageLoader;

    @Override
    public PippinMessageContext handles() {
        return PippinMessageContext.COMEBACK;
    }

    @Override
    public List<String> getMessagePool(PippinContext context) {
        return messageLoader.getMessages("COMEBACK", context.tone());
    }
}

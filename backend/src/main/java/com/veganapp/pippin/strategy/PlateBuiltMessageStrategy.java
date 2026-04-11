package com.veganapp.pippin.strategy;

import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.domain.PippinContext.PippinMessageContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Strategy para cuando el usuario arma su plato combinando Base+Proteína+Verdura+Salsa.
 * Los mensajes comentan sobre la elección nutricional.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlateBuiltMessageStrategy implements MessageStrategy {

    private final PippinMessageLoader messageLoader;

    @Override
    public PippinMessageContext handles() {
        return PippinMessageContext.PLATE_BUILT;
    }

    @Override
    public List<String> getMessagePool(PippinContext context) {
        return messageLoader.getMessages("PLATE_BUILT", context.tone());
    }
}

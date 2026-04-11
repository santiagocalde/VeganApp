package com.veganapp.pippin.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.domain.PippinContext.PippinMessageContext;
import com.veganapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy para check-ins regulares (sin hito especial).
 * Carga mensajes desde messages_es.json / messages_en.json.
 * Los mensajes varían por tono y motivación del usuario.
 * Se enriquecen con el número de días actual via String.format().
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CheckinMessageStrategy implements MessageStrategy {

    private final PippinMessageLoader messageLoader;

    @Override
    public PippinMessageContext handles() {
        return PippinMessageContext.CHECKIN;
    }

    @Override
    public List<String> getMessagePool(PippinContext context) {
        List<String> pool = messageLoader.getMessages("CHECKIN", context.tone(), context.motivation());
        
        // Interpolar el número de días en todos los mensajes del pool
        return pool.stream()
                .map(msg -> String.format(msg, context.currentDays()))
                .toList();
    }
}

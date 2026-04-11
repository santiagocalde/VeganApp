package com.veganapp.pippin.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import com.veganapp.common.config.AppProperties;
import com.veganapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cargador de mensajes de Sir Pippin desde JSON.
 *
 * Flujo:
 * 1. En construcción, carga el JSON especificado en AppProperties
 * 2. Cachea en memoria con TTL (evita recargar constantemente)
 * 3. Proporciona métodos para obtener mensajes por contexto/tono/motivación
 *
 * Patrones: Lazy Loading + Cache with TTL
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PippinMessageLoader {

    private final ResourceLoader resourceLoader;
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    private JsonNode messagesRoot;
    private long lastLoadTime = 0;
    private final Map<String, List<String>> messageCache = new ConcurrentHashMap<>();

    /**
     * Obtiene mensajes para un contexto y tono específicos.
     * Ejemplo: getMessages("CHECKIN", MOTIVATIONAL, HEALTH)
     */
    public List<String> getMessages(String contextKey, User.TonePref tone, User.Motivation motivation) {
        ensureLoaded();

        try {
            JsonNode contextNode = messagesRoot.get(contextKey);
            if (contextNode == null) {
                log.warn("Context not found in messages: {}", contextKey);
                return getFallbackMessage(contextKey);
            }

            JsonNode toneNode = contextNode.get(tone.name());
            if (toneNode == null) {
                log.warn("Tone not found for context {}: {}", contextKey, tone.name());
                return getFallbackMessage(contextKey);
            }

            JsonNode motivationNode = toneNode.get(motivation.name());
            if (motivationNode == null) {
                log.warn("Motivation not found for context {} / tone {}: {}", contextKey, tone.name(), motivation.name());
                return getFallbackMessage(contextKey);
            }

            return extractMessages(motivationNode);
        } catch (Exception e) {
            log.error("Error loading messages for context={}, tone={}, motivation={}", contextKey, tone, motivation, e);
            return getFallbackMessage(contextKey);
        }
    }

    /**
     * Obtiene mensajes para un contexto y tono específicos (sin motivación).
     * Usado para hitos y otros contextos que solo varían por tono.
     * Ejemplo: getMessages("MILESTONE_7", MOTIVATIONAL)
     */
    public List<String> getMessages(String contextKey, User.TonePref tone) {
        ensureLoaded();

        try {
            JsonNode contextNode = messagesRoot.get(contextKey);
            if (contextNode == null) {
                log.warn("Context not found in messages: {}", contextKey);
                return getFallbackMessage(contextKey);
            }

            JsonNode toneNode = contextNode.get(tone.name());
            if (toneNode == null) {
                log.warn("Tone not found for context {}: {}", contextKey, tone.name());
                return getFallbackMessage(contextKey);
            }

            return extractMessages(toneNode);
        } catch (Exception e) {
            log.error("Error loading messages for context={}, tone={}", contextKey, tone, e);
            return getFallbackMessage(contextKey);
        }
    }

    /**
     * Extrae lista de strings de un JsonNode (puede ser Array o Direct Lista)
     */
    private List<String> extractMessages(JsonNode node) {
        List<String> messages = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(item -> messages.add(item.asText()));
        } else if (node.isTextual()) {
            messages.add(node.asText());
        }
        return messages.isEmpty() ? getFallbackMessage("UNKNOWN") : messages;
    }

    /**
     * Fallback genérico si falta un mensaje
     */
    private List<String> getFallbackMessage(String context) {
        return List.of(
            "¡Check-in registrado! Seguís adelante.",
            "¡Excelente! Día siguiente.",
            "Confirmado. Continuá con la racha."
        );
    }

    /**
     * Carga el JSON si pasó el TTL o no está cargado aún
     */
    private synchronized void ensureLoaded() {
        long ttlMillis = appProperties.pippin().messagesCacheTtlMinutes() * 60_000L;
        if (messagesRoot == null || System.currentTimeMillis() - lastLoadTime > ttlMillis) {
            loadMessages();
        }
    }

    /**
     * Cargamiento real del archivo JSON
     */
    private void loadMessages() {
        try {
            String messagesPath = appProperties.pippin().messagesPath();
            Resource resource = resourceLoader.getResource(messagesPath);

            if (!resource.exists()) {
                log.error("Messages file not found: {}", messagesPath);
                throw new AppException(ErrorCode.INTERNAL_ERROR, "Archivo de mensajes de Pippin no encontrado");
            }

            messagesRoot = objectMapper.readTree(resource.getInputStream());
            lastLoadTime = System.currentTimeMillis();
            messageCache.clear(); // Limpiar cache al recargar

            log.info("Pippin messages loaded successfully from: {}", messagesPath);
        } catch (IOException e) {
            log.error("Error loading Pippin messages", e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Error cargando mensajes de Pippin: " + e.getMessage());
        }
    }
}

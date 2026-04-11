package com.veganapp.pippin.service;

import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.domain.PippinContext.PippinMessageContext;
import com.veganapp.pippin.dto.PippinReactionResponse;
import com.veganapp.pippin.strategy.MessageStrategy;
import com.veganapp.pippin.strategy.MessageStrategyRegistry;
import com.veganapp.common.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;

/**
 * Implementación de PippinService.
 *
 * Responsabilidades:
 * 1. Obtener strategy correcta desde MessageStrategyRegistry
 * 2. Obtener pool de mensajes
 * 3. Seleccionar uno aleatoriamente, evitando repetición (redis-backed)
 * 4. Mapear contexto a estado de animación
 * 5. En caso de fallo Redis, usar fallback (random simple)
 *
 * FIX #2: Resilencia con Redis fallback
 * Si Redis está caído o timeout, la función NO lanza excepción, devuelve random.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PippinServiceImpl implements PippinService {

    private static final String LAST_MSG_KEY_PREFIX = "pippin:last:";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final MessageStrategyRegistry strategyRegistry;
    private final RedisTemplate<String, String> redisTemplate;
    private final AppProperties appProperties;

    @Override
    public PippinReactionResponse getReaction(PippinContext context) {
        try {
            // Obtener la estrategia para este contexto
            MessageStrategy strategy = strategyRegistry.getStrategy(context.messageContext());
            List<String> pool = strategy.getMessagePool(context);

            if (pool.isEmpty()) {
                log.warn("Empty message pool for context: {}", context.messageContext());
                return new PippinReactionResponse("¡Check-in registrado!", appProperties.pippin().animationDefault());
            }

            // Seleccionar mensaje (con anti-repetición si Redis está disponible)
            String selectedMessage = selectNonRepeating(pool, context.userId(), context.messageContext());
            String animationState = resolveAnimationState(context.messageContext());

            log.debug("PippinReaction: userId={}, context={}, state={}", 
                    context.userId(), context.messageContext(), animationState);

            return new PippinReactionResponse(selectedMessage, animationState);
        } catch (Exception e) {
            log.error("Error generating Pippin reaction for context: {}", context.messageContext(), e);
            // Fallback seguro: devolver mensaje genérico
            return new PippinReactionResponse(
                "¡Check-in registrado!",
                appProperties.pippin().animationDefault()
            );
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MÉTODOS PRIVADOS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Selecciona un mensaje del pool evitando repetir el último enviado.
     *
     * FIX #2: Redis fallback
     * - Intenta leer desde Redis el último mensaje
     * - Si Redis falla, usa SimpleRandom (sin anti-repetición)
     * - Siempre guarda en Redis el mensaje seleccionado (con TTL)
     *
     * Algoritmo:
     * 1. Si pool tiene un elemento, devolverlo (no hay alternativa)
     * 2. Si pool tiene múltiples:
     *    a. Intentar obtener último mensaje desde Redis
     *    b. Si Redis falla, saltear verificación (fallback)
     *    c. Intentar 3 veces seleccionar uno diferente
     *    d. Si no puede, aceptar repetición
     * 3. Guardar seleccionado en Redis (con TTL)
     */
    private String selectNonRepeating(List<String> pool, Long userId, PippinMessageContext context) {
        if (pool.isEmpty()) {
            return "¡Check-in registrado!";
        }
        if (pool.size() == 1) {
            return pool.get(0);
        }

        String redisKey = LAST_MSG_KEY_PREFIX + userId + ":" + context.name();
        String selected = null;

        // FIX #2: Try-catch para Redis fallback
        try {
            String lastMessage = redisTemplate.opsForValue().get(redisKey);

            // Intentar seleccionar un diferente del último
            int attempts = 0;
            while (attempts < 3) {
                String candidate = pool.get(RANDOM.nextInt(pool.size()));
                if (!candidate.equals(lastMessage)) {
                    selected = candidate;
                    break;
                }
                attempts++;
            }

            // Si después de 3 intentos no encontró diferente, aceptar repetición
            if (selected == null) {
                selected = pool.get(RANDOM.nextInt(pool.size()));
                log.debug("Could not find different message for userId={}, using random", userId);
            }

        } catch (Exception redisException) {
            // FIX #2: Fallback suave si Redis está caído
            log.warn("Redis unavailable, falling back to random selection. userId={}, error: {}", 
                    userId, redisException.getClass().getSimpleName());
            selected = pool.get(RANDOM.nextInt(pool.size()));
        }

        // Guardar en Redis con TTL (intentar, pero no fallar si redis cae)
        try {
            Duration ttl = Duration.ofHours(appProperties.pippin().lastMessageTtlHours());
            redisTemplate.opsForValue().set(redisKey, selected, ttl);
        } catch (Exception e) {
            log.debug("Could not save last message to Redis (non-critical), userId={}", userId);
            // No fallar si no podemos guardar
        }

        return selected;
    }

    /**
     * Mapea los contextos de Pippin a estados de animación para el frontend.
     *
     * El frontend recibe "celebration" y sabe qué animación SVG reproducir.
     */
    private String resolveAnimationState(PippinMessageContext context) {
        return switch (context) {
            case CHECKIN -> "idle";
            case MILESTONE_3, MILESTONE_7, MILESTONE_14, MILESTONE_30, 
                 MILESTONE_60, MILESTONE_100, MILESTONE_180, MILESTONE_365 -> "celebration";
            case COMEBACK -> "surprised";
            case REMINDER -> "sarcastic";
            case STREAK_DANGER -> "sarcastic";
            case PLATE_BUILT -> "proud";
            case PLAN_COMPLETED -> "excited";
        };
    }
}

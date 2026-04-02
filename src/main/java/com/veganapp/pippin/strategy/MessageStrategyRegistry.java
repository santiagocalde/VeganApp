package com.veganapp.pippin.strategy;

import com.veganapp.pippin.domain.PippinContext.PippinMessageContext;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Registry que mapea cada PippinMessageContext a su MessageStrategy correspondiente.
 *
 * Patrón: Factory Method + Registry.
 * Al iniciar la aplicación (@PostConstruct), recorre todas las estrategias
 * inyectadas por Spring y las registra por su contexto.
 *
 * Para agregar una nueva estrategia: solo crear la clase, anotarla con @Component
 * e implementar MessageStrategy. El registry la detecta automáticamente.
 * No hay que tocar este archivo. (Principio Open/Closed)
 *
 * GRASP: Pure Fabrication — una clase de servicio puro sin análogo en el dominio,
 * pero que desacopla perfectamente el sistema.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageStrategyRegistry {

    private final List<MessageStrategy> strategies;
    private final MilestoneMessageStrategy milestoneStrategy;
    private final ReminderMessageStrategy reminderStrategy;

    private final Map<PippinMessageContext, MessageStrategy> registry =
            new EnumMap<>(PippinMessageContext.class);

    @PostConstruct
    public void buildRegistry() {
        log.info("Building MessageStrategyRegistry...");
        
        // Registrar estrategias estándar (una por contexto)
        strategies.forEach(strategy -> {
            registry.put(strategy.handles(), strategy);
            log.debug("Strategy registered: {} -> {}", strategy.handles(), strategy.getClass().getSimpleName());
        });

        // Registrar MilestoneStrategy para todos los contextos de hito
        if (milestoneStrategy != null) {
            milestoneStrategy.handlesAll().forEach(ctx -> registry.put(ctx, milestoneStrategy));
            log.debug("MilestoneMessageStrategy registered for {} contexts", milestoneStrategy.handlesAll().size());
        }

        // Registrar ReminderStrategy también para STREAK_DANGER
        if (reminderStrategy != null) {
            registry.put(reminderStrategy.handlesAlso(), reminderStrategy);
            log.debug("ReminderMessageStrategy registered for STREAK_DANGER");
        }

        log.info("MessageStrategyRegistry initialized. Total contexts registered: {}", registry.size());
    }

    /**
     * @throws IllegalStateException si no hay estrategia para el contexto dado.
     *         Esto indica un bug de configuración, no un error de usuario.
     */
    public MessageStrategy getStrategy(PippinMessageContext context) {
        MessageStrategy strategy = registry.get(context);
        if (strategy == null) {
            String errorMsg = String.format(
                "No MessageStrategy registered for context: %s. " +
                "Registered contexts: %s",
                context, registry.keySet()
            );
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        return strategy;
    }

    /**
     * Retorna el mapa completo de estrategias (util para debugging)
     */
    public Map<PippinMessageContext, MessageStrategy> getRegistry() {
        return Map.copyOf(registry);
    }
}

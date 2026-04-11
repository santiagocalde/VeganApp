package com.veganapp.pippin.service;

import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.dto.PippinReactionResponse;

/**
 * Contrato del servicio de Sir Pippin.
 * Orquesta la selección de mensajes sin conocer los detalles de las estrategias.
 *
 * GRASP: Facade — presenta la interfaz unificada al mundo exterior.
 */
public interface PippinService {
    /**
     * Devuelve la reacción de Sir Pippin para el contexto dado.
     * Garantiza que el mismo mensaje no se repita consecutivamente para el usuario
     * (usando Redis como cache, con fallback a random si Redis no está disponible).
     *
     * @param context contexto completo con tono, motivación, días, etc.
     * @return reacción con mensaje y estado de animación
     */
    PippinReactionResponse getReaction(PippinContext context);
}

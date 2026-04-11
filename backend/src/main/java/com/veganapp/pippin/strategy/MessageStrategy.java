package com.veganapp.pippin.strategy;

import com.veganapp.pippin.domain.PippinContext;

import java.util.List;

/**
 * Contrato del patrón Strategy para la selección de mensajes.
 *
 * Cada contexto (check-in, hito, regreso, recordatorio) tiene su propia
 * implementación de esta interfaz. Para agregar un nuevo tipo de mensaje:
 * 1. Agregar el enum en PippinContext.PippinMessageContext
 * 2. Crear una clase que implemente esta interfaz
 * 3. Registrarla en MessageStrategyRegistry
 * No tocar ninguna clase existente. (Principio Open/Closed)
 *
 * ISP (Interface Segregation Principle): una sola responsabilidad, un único método.
 */
public interface MessageStrategy {

    /**
     * @return el contexto que este Strategy maneja
     */
    PippinContext.PippinMessageContext handles();

    /**
     * Devuelve el pool de mensajes candidatos para el contexto,
     * tono y motivación dados. Los mensajes pueden incluir %d para interpolar días.
     *
     * @param context el contexto completo de la interacción
     * @return lista no vacía de mensajes candidatos
     */
    List<String> getMessagePool(PippinContext context);
}

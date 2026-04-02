package com.veganapp.pippin.dto;

/**
 * DTO de respuesta de Sir Pippin.
 *
 * - message: el texto que muestra la app (bubble de diálogo sobre el loro)
 * - animationState: el estado de la animación SVG del frontend
 *
 * Valores posibles de animationState:
 *   idle        -> Come lechuga tranquilo (pantalla principal sin novedad)
 *   celebration -> Aleteo rápido (hito de racha: 7, 30, 100 días)
 *   disappointed -> Cabeza baja, alita caída (racha rota)
 *   surprised   -> Pico abierto (primer check-in del día)
 *   sarcastic   -> Mira de reojo (recordatorio 48hs sin check-in)
 *   proud       -> Pecho inflado (armó su plato)
 *   excited     -> Salta (completó el planificador semanal)
 *
 * Immutable Record.
 */
public record PippinReactionResponse(
        String message,
        String animationState
) {}

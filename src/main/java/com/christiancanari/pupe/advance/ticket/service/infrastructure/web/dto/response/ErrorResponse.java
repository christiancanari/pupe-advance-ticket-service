package com.christiancanari.pupe.advance.ticket.service.infrastructure.web.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de respuesta estándar para errores expuestos por la API.
 *
 * <p>
 * Se utiliza para representar errores de negocio y técnicos
 * de forma consistente hacia el cliente.
 * </p>
 *
 * @param status    código HTTP del error
 * @param error     nombre corto del error (ej. BAD_REQUEST)
 * @param message   mensaje descriptivo del error
 * @param type      tipo de error interno del dominio
 * @param timestamp fecha y hora en que ocurrió el error
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String type,
        LocalDateTime timestamp
) {
}

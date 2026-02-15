package com.christiancanari.pupe.advance.ticket.service.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO para el procesamiento de tickets.
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public record ProcessTicketRequest(

        /**
         * Archivo Excel con las carpetas de Google Drive.
         */
        @NotNull(message = "El archivo Excel es obligatorio")
        MultipartFile file

) {
}

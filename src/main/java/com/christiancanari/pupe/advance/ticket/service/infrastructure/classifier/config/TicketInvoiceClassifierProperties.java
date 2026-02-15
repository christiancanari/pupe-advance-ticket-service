package com.christiancanari.pupe.advance.ticket.service.infrastructure.classifier.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Propiedades de configuración para los patrones de clasificación de comprobantes y facturas dentro de tickets PDF.
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Validated
@ConfigurationProperties(prefix = "ticket.invoice.classifier.patterns")
public record TicketInvoiceClassifierProperties(

        /**
         * Expresión regular para identificar facturas.
         */
        @NotBlank
        String invoiceRegex,

        /**
         * Expresión regular para identificar comprobantes.
         */
        @NotBlank
        String receiptRegex

) {
}

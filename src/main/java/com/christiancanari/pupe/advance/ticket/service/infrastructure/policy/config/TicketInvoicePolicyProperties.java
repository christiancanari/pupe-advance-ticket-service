package com.christiancanari.pupe.advance.ticket.service.infrastructure.policy.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

/**
 * Configuración de la política de validación de tickets.
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Validated
@ConfigurationProperties(prefix = "ticket.invoice.policy")
public record TicketInvoicePolicyProperties(

        /**
         * Conjunto de palabras clave obligatorias para validar un ticket.
         */
        @NotEmpty
        Set<String> keywords

) {}

package com.christiancanari.pupe.advance.ticket.service.infrastructure.policy.keyword;

import com.christiancanari.pupe.advance.ticket.service.domain.service.TicketInvoicePolicy;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.policy.config.TicketInvoicePolicyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementación de {@link TicketInvoicePolicy} basada en
 * palabras clave configuradas externamente.
 *
 * <p>
 * Actúa como un adaptador de infraestructura dentro de la
 * arquitectura hexagonal.
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class KeywordTicketInvoicePolicy implements TicketInvoicePolicy {

    private final TicketInvoicePolicyProperties properties;

    public KeywordTicketInvoicePolicy(TicketInvoicePolicyProperties properties) {
        this.properties = properties;
    }

    /**
     * Valida si el nombre de un archivo corresponde a un ticket válido
     * según las palabras clave configuradas.
     *
     * @param fileName nombre del archivo PDF
     * @return {@code true} si cumple la política; {@code false} en caso contrario
     */
    @Override
    public boolean isValid(String fileName) {

        return Optional.ofNullable(fileName)
                .filter(name -> !name.isBlank())
                .map(String::toLowerCase)
                .map(this::matchesAnyKeyword)
                .orElse(false);
    }

    private boolean matchesAnyKeyword(String fileName) {

        boolean valid = properties.keywords()
                .stream()
                .anyMatch(fileName::contains);

        log.debug("Validación de archivo PDF '{}' → {}", fileName, valid ? "VÁLIDO" : "NO VÁLIDO");

        return valid;
    }

}
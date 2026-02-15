package com.christiancanari.pupe.advance.ticket.service.domain.service;

/**
 * Política de dominio para validar archivos de tickets según reglas de negocio.
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TicketInvoicePolicy {

    /**
     * Valida si un archivo cumple con las reglas
     * para ser procesado como ticket.
     *
     * @param fileName nombre del archivo
     * @return true si el archivo es válido
     */
    boolean isValid(String fileName);

}

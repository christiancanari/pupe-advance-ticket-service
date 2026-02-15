package com.christiancanari.pupe.advance.ticket.service.application.service;

import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketInvoice;

import java.util.List;

/**
 * Servicio de aplicación encargado de procesar una carpeta de tickets
 * y retornar los tickets válidos extraídos.
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public interface FolderTicketProcessor {

    /**
     * Procesa una carpeta identificada por su nombre.
     *
     * @param folderName nombre de la carpeta a procesar
     * @return lista de tickets extraídos
     */
    List<TicketInvoice> process(String folderName);

}

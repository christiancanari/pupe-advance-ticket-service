package com.christiancanari.pupe.advance.ticket.service.domain.model;

/**
 * Representa la información resultante de un ticket procesado.
 *
 * <p>
 * Contiene los valores relevantes extraídos desde un archivo PDF,
 * asociados a la carpeta y archivo de origen.
 * </p>
 *
 * @param sourceFileName      nombre del archivo PDF original
 * @param processedFileName   nombre del archivo o carpeta procesada
 * @param facturaContent      contenido identificado como factura
 * @param comprobanteContent  contenido identificado como comprobante
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public record TicketInvoice(
        String sourceFileName,
        String processedFileName,
        String facturaContent,
        String comprobanteContent
) {
}

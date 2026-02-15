package com.christiancanari.pupe.advance.ticket.service.domain.classifier;

/**
 * Contiene los valores clasificados extraídos desde un ticket.
 *
 * <p>
 * Representa el resultado del proceso de clasificación aplicado
 * al contenido de un ticket (por ejemplo, texto de un PDF).
 * </p>
 *
 * @param invoices  valor identificado como factura
 * @param receipts valor identificado como boleta o recibo
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public record ClassifiedTicketValues(
        String invoices,
        String receipts
) {

    /**
     * Retorna una instancia vacía del resultado de clasificación.
     *
     * @return instancia vacía de {@link ClassifiedTicketValues}
     */
    public static ClassifiedTicketValues empty() {
        return new ClassifiedTicketValues("", "");
    }

}

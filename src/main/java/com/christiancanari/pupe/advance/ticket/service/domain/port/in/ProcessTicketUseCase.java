package com.christiancanari.pupe.advance.ticket.service.domain.port.in;

import java.io.InputStream;

/**
 * Caso de uso de aplicación encargado de procesar un archivo Excel
 * con información de tickets de adelanto y generar un nuevo archivo
 * Excel con el resultado del procesamiento.
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ProcessTicketUseCase {

    /**
     * Procesa un archivo Excel recibido como flujo de entrada,
     * aplicando las reglas de negocio correspondientes y generando
     * un nuevo archivo Excel como resultado.
     *
     * @param inputStream flujo de entrada del archivo Excel original
     * @return arreglo de bytes que representa el archivo Excel procesado
     */
    byte[] process(InputStream inputStream);

}

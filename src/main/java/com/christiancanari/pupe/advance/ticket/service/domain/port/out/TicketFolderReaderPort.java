package com.christiancanari.pupe.advance.ticket.service.domain.port.out;

import java.io.InputStream;
import java.util.List;

/**
 * Puerto de salida para la lectura de nombres de carpetas desde una fuente de datos.
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TicketFolderReaderPort {

    /**
     * Obtiene los nombres de carpetas a procesar.
     *
     * @param inputStream flujo de entrada de la fuente de datos
     * @return lista de nombres de carpetas
     */
    List<String> readFolderNames(InputStream inputStream);

}

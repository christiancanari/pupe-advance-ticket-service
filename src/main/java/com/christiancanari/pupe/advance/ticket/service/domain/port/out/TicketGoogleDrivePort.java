package com.christiancanari.pupe.advance.ticket.service.domain.port.out;

import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la interacción con Google Drive.
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TicketGoogleDrivePort {

    /**
     * Busca el identificador de una carpeta por su nombre.
     *
     * @param folderName nombre de la carpeta
     * @return identificador de la carpeta si existe
     */
    Optional<String> findFolderIdByName(String folderName);

    /**
     * Obtiene el identificador de la subcarpeta de tickets
     * asociada a una carpeta padre.
     *
     * @param parentFolderId identificador de la carpeta padre
     * @return identificador de la subcarpeta de tickets si existe
     */
    Optional<String> findTicketFolderId(String parentFolderId);

    /**
     * Lista los archivos PDF contenidos en una carpeta.
     *
     * @param folderId identificador de la carpeta
     * @return lista de archivos PDF encontrados
     */
    List<TicketFile> listPdfFiles(String folderId);

    /**
     * Descarga un archivo desde Google Drive.
     *
     * @param fileId identificador del archivo
     * @return flujo de entrada del archivo
     */
    InputStream downloadFile(String fileId);

}

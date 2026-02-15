package com.christiancanari.pupe.advance.ticket.service.infrastructure.client.adapter;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.domain.port.out.TicketGoogleDrivePort;
import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketFile;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Adapter de infraestructura encargado de la comunicación con Google Drive.
 *
 * <p>
 * Implementa el puerto de salida {@link TicketGoogleDrivePort} y utiliza
 * la API oficial de Google Drive para:
 * </p>
 *
 * <ul>
 *     <li>Buscar carpetas por nombre</li>
 *     <li>Localizar subcarpetas específicas de tickets</li>
 *     <li>Listar archivos PDF dentro de una carpeta</li>
 *     <li>Descargar archivos desde Google Drive</li>
 * </ul>
 *
 * <p>
 * Cualquier error técnico proveniente de la API de Google Drive
 * es encapsulado en {@link CoreTechnicalException}.
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Repository
public class TicketGoogleDriveAdapter implements TicketGoogleDrivePort {

    private final Drive drive;

    /**
     * Construye el adapter con una instancia configurada del cliente {@link Drive} de Google.
     *
     * @param drive cliente oficial de Google Drive
     */
    public TicketGoogleDriveAdapter(Drive drive) {
        this.drive = drive;
    }

    /**
     * Busca el identificador de una carpeta en Google Drive a partir de su nombre.
     *
     * @param folderName nombre de la carpeta a buscar
     * @return {@link Optional} con el ID de la carpeta si existe; vacío en caso contrario
     */
    @Override
    public Optional<String> findFolderIdByName(String folderName) {

        log.info("🔍 [Drive] Buscando carpeta por nombre: {}", folderName);

        try {
            String query = String.format("mimeType='application/vnd.google-apps.folder' " +
                            "and name contains '%s' and trashed=false", folderName);

            FileList result = drive.files().list()
                    .setQ(query)
                    .setFields("files(id)")
                    .setSupportsAllDrives(true)
                    .setIncludeItemsFromAllDrives(true)
                    .execute();

            if (result.getFiles().isEmpty()) {
                log.warn("[Drive] Carpeta no encontrada: {}", folderName);
                return Optional.empty();
            }

            String folderId = result.getFiles().get(0).getId();
            log.debug("[Drive] Carpeta encontrada: {} → id={}", folderName, folderId);

            return Optional.of(folderId);

        } catch (Exception ex) {
            log.error("[Drive] Error buscando carpeta {}", folderName, ex);
            throw new CoreTechnicalException("Error accediendo a Google Drive al buscar la carpeta",
                    ErrorType.DRIVE_ACCESS_ERROR, ex);
        }

    }

    /**
     * Busca la subcarpeta de tickets dentro de una carpeta padre.
     *
     * @param parentFolderId identificador de la carpeta padre
     * @return {@link Optional} con el ID de la subcarpeta de tickets si existe
     */
    @Override
    public Optional<String> findTicketFolderId(String parentFolderId) {

        log.info("🔍 [Drive] Buscando subcarpeta de Tickets en parentId={}", parentFolderId);

        try {
            String query = String.format(
                    "'%s' in parents and mimeType='application/vnd.google-apps.folder' " +
                            "and name contains 'Tickets en general' and trashed=false",
                    parentFolderId
            );

            FileList result = drive.files().list()
                    .setQ(query)
                    .setFields("files(id)")
                    .setSupportsAllDrives(true)
                    .setIncludeItemsFromAllDrives(true)
                    .execute();

            if (result.getFiles().isEmpty()) {
                log.warn("[Drive] Subcarpeta Tickets no encontrada en parentId={}", parentFolderId);
                return Optional.empty();
            }

            String ticketFolderId = result.getFiles().get(0).getId();
            log.debug("[Drive] Subcarpeta Tickets encontrada → id={}", ticketFolderId);

            return Optional.of(ticketFolderId);

        } catch (Exception ex) {
            log.error("[Drive] Error buscando subcarpeta Tickets", ex);
            throw new CoreTechnicalException("Error accediendo a Google Drive al buscar la subcarpeta de Tickets",
                    ErrorType.DRIVE_ACCESS_ERROR, ex);
        }

    }

    /**
     * Lista todos los archivos PDF contenidos en una carpeta de Google Drive.
     *
     * @param folderId identificador de la carpeta
     * @return lista de archivos PDF encontrados
     */
    @Override
    public List<TicketFile> listPdfFiles(String folderId) {

        log.info("📄 [Drive] Listando archivos PDF del folder {}", folderId);

        try {
            String query = String.format(
                    "'%s' in parents and mimeType='application/pdf' and trashed=false",
                    folderId
            );

            FileList result = drive.files().list()
                    .setQ(query)
                    .setFields("files(id, name)")
                    .setSupportsAllDrives(true)
                    .setIncludeItemsFromAllDrives(true)
                    .execute();

            List<TicketFile> files = Optional.ofNullable(result.getFiles())
                    .orElse(List.of())
                    .stream()
                    .map(file -> new TicketFile(file.getId(), file.getName()))
                    .toList();

            log.info("[Drive] PDFs encontrados: {}", files.size());

            return files;

        } catch (Exception ex) {
            log.error("[Drive] Error listando PDFs del folder {}", folderId, ex);
            throw new CoreTechnicalException("Error accediendo a Google Drive al listar archivos PDF",
                    ErrorType.DRIVE_ACCESS_ERROR, ex);
        }

    }

    /**
     * Descarga un archivo desde Google Drive.
     *
     * @param fileId identificador del archivo
     * @return {@link InputStream} del archivo descargado
     */
    @Override
    public InputStream downloadFile(String fileId) {

        log.info("[Drive] Descargando archivo con id={}", fileId);

        try {
            return drive.files()
                    .get(fileId)
                    .executeMediaAsInputStream();

        } catch (Exception ex) {
            log.error("[Drive] Error descargando archivo {}", fileId, ex);
            throw new CoreTechnicalException("Error accediendo a Google Drive al descargar el archivo",
                    ErrorType.DRIVE_ACCESS_ERROR, ex);
        }

    }

}

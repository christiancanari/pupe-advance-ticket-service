package com.christiancanari.pupe.advance.ticket.service.application.service;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketFile;
import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketInvoice;
import com.christiancanari.pupe.advance.ticket.service.domain.port.out.TicketGoogleDrivePort;
import com.christiancanari.pupe.advance.ticket.service.domain.service.TicketInvoiceExtractor;
import com.christiancanari.pupe.advance.ticket.service.domain.service.TicketInvoicePolicy;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreBusinessException;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio {@link FolderTicketProcessor}.
 *
 * <p>
 * Se encarga de procesar una carpeta de Google Drive:
 * localizar la carpeta de tickets, filtrar PDFs válidos
 * y extraer la información de cada archivo.
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class FolderTicketProcessorImpl implements FolderTicketProcessor {

    private final TicketGoogleDrivePort ticketGoogleDrivePort;
    private final TicketInvoiceExtractor ticketInvoiceExtractor;
    private final TicketInvoicePolicy ticketInvoicePolicy;

    /**
     * Constructor con inyección de dependencias.
     */
    public FolderTicketProcessorImpl(
            TicketGoogleDrivePort ticketGoogleDrivePort,
            TicketInvoiceExtractor ticketInvoiceExtractor,
            TicketInvoicePolicy ticketInvoicePolicy
    ) {
        this.ticketGoogleDrivePort = ticketGoogleDrivePort;
        this.ticketInvoiceExtractor = ticketInvoiceExtractor;
        this.ticketInvoicePolicy = ticketInvoicePolicy;
    }

    /**
     * Procesa una carpeta y retorna los tickets válidos encontrados.
     *
     * @param folderName nombre de la carpeta
     * @return lista de tickets procesados
     */
    @Override
    public List<TicketInvoice> process(String folderName) {

        log.debug("➡ Procesando carpeta: {}", folderName);

        try {
            Optional<String> folderId =
                    ticketGoogleDrivePort.findFolderIdByName(folderName);

            if (folderId.isEmpty()) {
                log.warn("Carpeta no encontrada en Google Drive: {}", folderName);
                return List.of();
            }

            Optional<String> ticketsFolderId =
                    ticketGoogleDrivePort.findTicketFolderId(folderId.get());

            if (ticketsFolderId.isEmpty()) {
                log.warn("Subcarpeta 'Tickets' no encontrada en {}", folderName);
                return List.of();
            }

            List<TicketFile> pdfFiles =
                    ticketGoogleDrivePort.listPdfFiles(ticketsFolderId.get());

            if (pdfFiles.isEmpty()) {
                log.info("No se encontraron PDFs en la carpeta: {}", folderName);
                return List.of();
            }

            return pdfFiles.stream()
                    .filter(pdf -> ticketInvoicePolicy.isValid(pdf.name()))
                    .map(pdf -> extractInvoice(folderName, pdf))
                    .flatMap(Optional::stream)
                    .toList();

        } catch (CoreBusinessException | CoreTechnicalException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error técnico procesando la carpeta {}", folderName, ex);
            throw new CoreTechnicalException(
                    "Error procesando la carpeta: " + folderName,
                    ErrorType.DRIVE_ACCESS_ERROR,
                    ex
            );
        }
    }

    /**
     * Extrae la información de un archivo PDF individual.
     *
     * @param folderName nombre de la carpeta
     * @param ticketFile archivo PDF a procesar
     * @return ticket extraído si el procesamiento fue exitoso
     */
    private Optional<TicketInvoice> extractInvoice(String folderName, TicketFile ticketFile) {

        try (InputStream pdfStream =
                     ticketGoogleDrivePort.downloadFile(ticketFile.id())) {

            return Optional.ofNullable(
                    ticketInvoiceExtractor.extract(
                            folderName,
                            ticketFile.name(),
                            pdfStream
                    )
            );

        } catch (Exception ex) {
            log.error("Error procesando PDF {}", ticketFile.name(), ex);
            throw new CoreTechnicalException(
                    "Error procesando archivo PDF: " + ticketFile.name(),
                    ErrorType.PDF_PROCESSING_ERROR,
                    ex
            );
        }
    }

}

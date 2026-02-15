package com.christiancanari.pupe.advance.ticket.service.infrastructure.web.controller;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.domain.port.in.ProcessTicketUseCase;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.dto.request.ProcessTicketRequest;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller REST para el procesamiento de tickets.
 *
 * <p>
 * Adapter de entrada en una arquitectura hexagonal.
 * Se encarga únicamente de recibir la petición HTTP, validar el request y delegar al caso de uso.
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/advances")
@Tag(
        name = "Advance Tickets",
        description = "Procesamiento de tickets y generación de reportes Excel"
)
public class AdvanceTicketController {

    /**
     * MIME type estándar para archivos Excel (.xlsx)
     */
    private static final String EXCEL_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ProcessTicketUseCase processTicketUseCase;

    public AdvanceTicketController(ProcessTicketUseCase processTicketUseCase) {
        this.processTicketUseCase = processTicketUseCase;
    }

    /**
     * Procesa un archivo Excel que contiene nombres de carpetas,
     * busca los tickets PDF asociados y genera un Excel consolidado.
     *
     * @param request request multipart que contiene el archivo Excel
     * @return archivo Excel generado como resultado
     */
    @Operation(
            summary = "Procesar tickets desde Excel",
            description = """
                    Recibe un archivo Excel con nombres de carpetas.
                    El sistema buscará los tickets PDF en Google Drive,
                    extraerá la información relevante y devolverá un
                    archivo Excel consolidado.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Archivo Excel generado correctamente",
            content = @Content(
                    mediaType = EXCEL_MIME,
                    schema = @Schema(type = "string", format = "binary")
            )
    )
    @ApiResponse(responseCode = "400", description = "Archivo no enviado o inválido")
    @ApiResponse(responseCode = "500", description = "Error interno del sistema")
    @PostMapping(
            value = "/process-ticket",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = EXCEL_MIME
    )
    public ResponseEntity<byte[]> processTicket(
            @Valid @ModelAttribute ProcessTicketRequest request
    ) {

        MultipartFile file = request.file();

        if (file == null || file.isEmpty()) {
            throw new CoreRequestException("El archivo Excel es obligatorio", ErrorType.EXCEL_INVALID);
        }

        log.info("Solicitud recibida: procesamiento de tickets");

        byte[] result = processTicketUseCase.process(toInputStream(file));

        log.info("Procesamiento finalizado exitosamente");

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + buildResultFilename() + "\""
                )
                .contentType(MediaType.parseMediaType(EXCEL_MIME))
                .body(result);
    }

    /**
     * Convierte un {@link MultipartFile} en {@link InputStream}.
     *
     * <p>
     * Cualquier error de lectura se considera un error de entrada HTTP.
     * </p>
     *
     * @param file archivo multipart validado
     * @return flujo de entrada del archivo
     */
    private InputStream toInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (Exception ex) {
            log.error("Error leyendo archivo Excel de entrada", ex);
            throw new CoreRequestException("No se pudo leer el archivo Excel de entrada", ErrorType.EXCEL_INVALID, ex);
        }
    }

    /**
     * Genera el nombre del archivo Excel de salida.
     *
     * <p>
     * Formato:
     * <pre>resultado_yyyyMMdd_HHmmss.xlsx</pre>
     * </p>
     *
     * @return nombre del archivo
     */
    private String buildResultFilename() {
        return "resultado_"
                + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".xlsx";
    }

}

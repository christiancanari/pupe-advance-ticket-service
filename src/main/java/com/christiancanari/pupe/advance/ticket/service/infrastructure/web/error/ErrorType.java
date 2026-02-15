package com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error;

import org.springframework.http.HttpStatus;

/**
 * Catálogo centralizado de tipos de error expuestos por la API.
 *
 * <p>
 * Cada {@link ErrorType} define el tipo de error funcional o técnico
 * y su correspondiente {@link HttpStatus}, permitiendo una
 * respuesta HTTP consistente.
 * </p>
 *
 * <p>
 * Clasificación:
 * <ul>
 *     <li>Errores de negocio</li>
 *     <li>Errores de seguridad</li>
 *     <li>Errores técnicos</li>
 * </ul>
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ErrorType {

    /* =======================
     * ERRORES DE NEGOCIO
     * ======================= */

    /** Archivo Excel inválido o con formato incorrecto */
    EXCEL_INVALID(HttpStatus.BAD_REQUEST),

    /** No se encontraron carpetas para procesar */
    NO_FOLDERS_FOUND(HttpStatus.BAD_REQUEST),

    /** Recurso solicitado no encontrado */
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),

    /** Conflicto de estado o duplicidad */
    CONFLICT(HttpStatus.CONFLICT),

    /* =======================
     * ERRORES DE SEGURIDAD
     * ======================= */

    /** Usuario no autenticado */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),

    /** Usuario autenticado sin permisos */
    FORBIDDEN(HttpStatus.FORBIDDEN),

    /* =======================
     * ERRORES TÉCNICOS
     * ======================= */

    /** Error al procesar archivos PDF */
    PDF_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

    /** Error al generar el archivo de salida */
    FILE_GENERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

    /** Error de comunicación con Google Drive */
    DRIVE_ACCESS_ERROR(HttpStatus.SERVICE_UNAVAILABLE),

    /** Payload excede el tamaño permitido */
    PAYLOAD_TOO_LARGE(HttpStatus.valueOf(413)),

    /* =======================
     * ERROR GENÉRICO
     * ======================= */

    /** Error inesperado del sistema */
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorType(HttpStatus status) {
        this.status = status;
    }

    /**
     * Retorna el {@link HttpStatus} asociado al tipo de error.
     *
     * @return estado HTTP correspondiente
     */
    public HttpStatus status() {
        return status;
    }
}

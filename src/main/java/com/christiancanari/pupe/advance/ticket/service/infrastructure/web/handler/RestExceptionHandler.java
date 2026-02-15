package com.christiancanari.pupe.advance.ticket.service.infrastructure.web.handler;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.dto.response.ErrorResponse;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Manejador global de excepciones REST.
 *
 * <p>
 * Centraliza la traducción de excepciones a respuestas HTTP
 * normalizadas mediante {@link ErrorResponse}.
 * </p>
 *
 * <p>
 * Captura:
 * <ul>
 *     <li>{@link CoreException}: errores controlados del dominio o infraestructura</li>
 *     <li>{@link Exception}: errores no previstos</li>
 * </ul>
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Maneja excepciones controladas del sistema.
     *
     * @param ex excepción base del sistema
     * @return respuesta HTTP estructurada
     */
    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ErrorResponse> handleCore(CoreException ex) {

        log.error("CoreException [{}] - {}", ex.getType(), ex.getMessage(), ex);

        return build(ex.getType(), ex);
    }

    /**
     * Maneja errores inesperados no controlados.
     *
     * @param ex excepción no prevista
     * @return respuesta HTTP genérica de error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {

        log.error("Unexpected error", ex);

        return build(ErrorType.UNEXPECTED_ERROR, ex);
    }

    /**
     * Construye la respuesta estándar de error.
     *
     * @param type tipo de error definido por la aplicación
     * @param ex excepción capturada
     * @return {@link ResponseEntity} con {@link ErrorResponse}
     */
    private ResponseEntity<ErrorResponse> build(ErrorType type, Exception ex) {

        ErrorResponse response = new ErrorResponse(
                type.status().value(),
                type.status().getReasonPhrase(),
                ex.getMessage(),
                type.name(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(type.status())
                .body(response);
    }

}

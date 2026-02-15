package com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;

/**
 * Excepción utilizada para representar errores de entrada HTTP inválida.
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public class CoreRequestException extends CoreException {

    /**
     * Crea una excepción de request inválido.
     *
     * @param message mensaje descriptivo del error
     * @param type tipo de error expuesto por la API
     */
    public CoreRequestException(String message, ErrorType type) {
        super(message, type);
    }

    /**
     * Crea una excepción de request inválido con causa raíz.
     *
     * @param message mensaje descriptivo del error
     * @param type tipo de error expuesto por la API
     * @param cause excepción original
     */
    public CoreRequestException(String message, ErrorType type, Throwable cause) {
        super(message, type, cause);
    }

}

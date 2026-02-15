package com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;

/**
 * Excepción base de la aplicación para el manejo unificado de errores.
 *
 * <p>
 * Todas las excepciones expuestas por la capa web deben
 * extender de esta clase para garantizar:
 * </p>
 * <ul>
 *     <li>Un tipo de error estandarizado ({@link ErrorType})</li>
 *     <li>Mapeo consistente a códigos HTTP</li>
 *     <li>Respuestas de error uniformes</li>
 * </ul>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class CoreException extends RuntimeException {

    /**
     * Tipo de error asociado a la excepción.
     */
    private final ErrorType type;

    /**
     * Constructor base con mensaje y tipo de error.
     *
     * @param message mensaje descriptivo del error
     * @param type tipo de error asociado
     */
    protected CoreException(String message, ErrorType type) {
        super(message);
        this.type = type;
    }

    /**
     * Constructor base con causa raíz.
     *
     * @param message mensaje descriptivo del error
     * @param type tipo de error asociado
     * @param cause excepción original
     */
    protected CoreException(String message, ErrorType type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    /**
     * Retorna el tipo de error asociado.
     *
     * @return tipo de error
     */
    public ErrorType getType() {
        return type;
    }

}

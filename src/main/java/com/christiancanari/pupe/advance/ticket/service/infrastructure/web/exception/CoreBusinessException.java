package com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;

/**
 * Excepción de negocio utilizada para representar errores derivados de reglas, validaciones o estados inválidos
 * del dominio expuestos por la API.
 *

 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
public class CoreBusinessException extends CoreException {

    /**
     * Crea una excepción de negocio con un mensaje y tipo de error.
     *
     * @param message mensaje descriptivo del error
     * @param type tipo de error asociado
     */
    public CoreBusinessException(String message, ErrorType type) {
        super(message, type);
    }

    /**
     * Crea una excepción de negocio con causa raíz.
     *
     * @param message mensaje descriptivo del error
     * @param type tipo de error asociado
     * @param cause excepción original
     */
    public CoreBusinessException(String message, ErrorType type, Throwable cause) {
        super(message, type, cause);
    }

}

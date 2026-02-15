package com.christiancanari.pupe.advance.ticket.service.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Propiedades de configuración para la integración con Google Drive.
 *
 * @param applicationName nombre de la aplicación reportado a Google Drive
 * @param credentials     configuración de credenciales de acceso
 * @param scopes          scopes habilitados para el acceso a Google Drive
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Validated
@ConfigurationProperties(prefix = "google.drive")
public record GoogleDriveProperties(

        @NotBlank
        String applicationName,

        Credentials credentials,

        @NotEmpty
        List<String> scopes
) {

    /**
     * Configuración de credenciales para Google Drive.
     *
     * @param location ubicación del archivo de credenciales
     */
    public record Credentials(

            @NotBlank
            String location
    ) {}
}

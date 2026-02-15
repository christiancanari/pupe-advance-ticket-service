package com.christiancanari.pupe.advance.ticket.service.infrastructure.config;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Configuración de infraestructura para la integración con Google Drive.
 *
 * <p>
 * Inicializa y expone el cliente {@link Drive} como un bean administrado por Spring,
 * utilizando propiedades externas definidas en {@code application.yml}.
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class GoogleDriveConfig {

    private final GoogleDriveProperties googleDriveProperties;
    private final ResourceLoader resourceLoader;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param properties     propiedades de configuración de Google Drive
     * @param resourceLoader cargador de recursos para credenciales externas
     */
    public GoogleDriveConfig(GoogleDriveProperties properties, ResourceLoader resourceLoader) {
        this.googleDriveProperties = properties;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Crea y configura el cliente {@link Drive} de Google.
     *
     * @return cliente configurado de Google Drive
     */
    @Bean
    public Drive driveService() {

        log.info("[DriveConfig] Inicializando cliente de Google Drive");

        try {
            Resource credentialsResource = resourceLoader.getResource(googleDriveProperties.credentials().location());

            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(credentialsResource.getInputStream())
                    .createScoped(googleDriveProperties.scopes());

            Drive drive = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName(googleDriveProperties.applicationName()).build();

            log.info("[DriveConfig] Cliente de Google Drive inicializado correctamente");
            return drive;

        } catch (Exception ex) {
            log.error("[DriveConfig] Error al inicializar Google Drive", ex);
            throw new CoreTechnicalException(
                    "No fue posible inicializar el cliente de Google Drive",
                    ErrorType.DRIVE_ACCESS_ERROR,
                    ex
            );
        }

    }

}

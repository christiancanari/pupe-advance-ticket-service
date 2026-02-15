package com.christiancanari.pupe.advance.ticket.service.infrastructure.config;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ResourceLoader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleDriveConfigTest {

    private GoogleDriveProperties properties;
    private ResourceLoader resourceLoader;

    private GoogleDriveConfig config;

    @BeforeEach
    void setUp() {
        properties = mock(GoogleDriveProperties.class);
        resourceLoader = mock(ResourceLoader.class);

        config = new GoogleDriveConfig(properties, resourceLoader);
    }

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException cuando ocurre un error al inicializar Google Drive")
    void shouldThrowCoreTechnicalExceptionWhenInitializationFails() {

        when(properties.credentials())
                .thenReturn(new GoogleDriveProperties.Credentials("classpath:credentials.json"));

        when(resourceLoader.getResource(any()))
                .thenThrow(new RuntimeException("Error cargando recurso"));

        CoreTechnicalException exception = assertThrows(
                CoreTechnicalException.class,
                () -> config.driveService()
        );

        assertEquals(
                "No fue posible inicializar el cliente de Google Drive",
                exception.getMessage()
        );
        assertEquals(ErrorType.DRIVE_ACCESS_ERROR, exception.getType());
        assertNotNull(exception.getCause());
    }

}

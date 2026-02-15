package com.christiancanari.pupe.advance.ticket.service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger / OpenAPI para el microservicio
 * <b>Advance Ticket</b> de Acor System.
 *
 * <p>
 * Esta clase define la metadata expuesta a través de Swagger UI y OpenAPI,
 * permitiendo documentar y visualizar los endpoints REST del microservicio.
 * </p>
 *
 * <p>
 * La información configurada incluye:
 * <ul>
 *     <li>Nombre y descripción del servicio</li>
 *     <li>Versión del API</li>
 *     <li>Información de contacto</li>
 *     <li>Licencia</li>
 * </ul>
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class SwaggerConfig {

    private static final Logger log = LoggerFactory.getLogger(SwaggerConfig.class);

    /**
     * Define la configuración principal de OpenAPI utilizada por Swagger UI.
     *
     * <p>
     * El bean {@link OpenAPI} es detectado automáticamente por SpringDoc
     * para generar la documentación interactiva del API.
     * </p>
     *
     * @return instancia personalizada de {@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenAPI() {

        log.info("[SwaggerConfig] Inicializando configuración de OpenAPI");

        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Christian Canari – Advance Ticket API")
                        .description("Microservicio encargado de procesar tickets, extraer información " +
                                        "de facturas y comprobantes desde archivos PDF y generar " +
                                        "reportes estructurados."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Desarrollador Christian Canari")
                                .email("christian@christiancanari.com")
                                .url("https://christiancanari.com")
                        )
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")
                        )
                );

        log.info("[SwaggerConfig] Configuración de OpenAPI cargada correctamente");
        return openAPI;
    }

}

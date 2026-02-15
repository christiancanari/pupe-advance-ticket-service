package com.christiancanari.pupe.advance.ticket.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Clase principal de arranque de la aplicación Advance Ticket.
 *
 * <p>
 * Inicializa el contexto de Spring Boot y habilita:
 * <ul>
 *     <li>Escaneo automático de componentes</li>
 *     <li>Registro de beans de configuración (@ConfigurationProperties)</li>
 *     <li>Arranque del servidor web embebido</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Nota de arquitectura:</b><br>
 * Esta clase pertenece a la capa de <i>bootstrap</i>.
 * No debe contener lógica de negocio ni dependencias propias de capas de dominio o infraestructura.
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.christiancanari.pupe.advance.ticket.service.infrastructure")
public class PupeAdvanceTicketServiceApplication {

	/**
	 * Punto de entrada principal de la aplicación.
	 *
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(PupeAdvanceTicketServiceApplication.class, args);
	}
}

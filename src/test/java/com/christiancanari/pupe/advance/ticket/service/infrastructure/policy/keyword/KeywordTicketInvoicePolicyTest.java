package com.christiancanari.pupe.advance.ticket.service.infrastructure.policy.keyword;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.policy.config.TicketInvoicePolicyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeywordTicketInvoicePolicyTest {

    private KeywordTicketInvoicePolicy policy;

    @BeforeEach
    void setUp() {
        TicketInvoicePolicyProperties properties =
                new TicketInvoicePolicyProperties(
                        Set.of("pr", "-pr", "ir", "-ir", "peru rail", "perurail")
                );

        policy = new KeywordTicketInvoicePolicy(properties);
    }

    @ParameterizedTest(name = "[{index}] fileName=\"{0}\" → valid={1}")
    @DisplayName("Debe validar correctamente el nombre del archivo según keywords")
    @CsvSource({
            // Casos válidos
            "Factura_PERURAIL_2024.pdf, true",
            "FACTURA-Pr-2024.PDF, true",

            // Casos inválidos
            "Factura_SUNAT_2024.pdf, false",
            "'', false",
            ", false"
    })
    void shouldValidateFileNameAccordingToKeywords(String fileName, boolean expected) {

        boolean result = policy.isValid(fileName);

        assertEquals(expected, result);
    }
}

package com.christiancanari.pupe.advance.ticket.service.infrastructure.classifier.regex;


import com.christiancanari.pupe.advance.ticket.service.domain.classifier.ClassifiedTicketValues;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.classifier.config.TicketInvoiceClassifierProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para {@link RegexTicketInvoiceClassifier}.
 *
 * <p>
 * No levanta contexto Spring.
 * Valida únicamente la lógica de clasificación por regex.
 * </p>
 */
class RegexTicketInvoiceClassifierTest {

    private RegexTicketInvoiceClassifier classifier;

    @BeforeEach
    void setUp() {
        TicketInvoiceClassifierProperties properties =
                new TicketInvoiceClassifierProperties(
                        "F\\d{3}-\\d{8}",   // invoice regex
                        "B\\d{3}-\\d{8}"    // receipt regex
                );

        classifier = new RegexTicketInvoiceClassifier(properties);
    }

    @Test
    void shouldClassifyInvoiceWhenTextContainsInvoicePattern() {
        String text = "Factura emitida F123-12345678 por servicios.";

        ClassifiedTicketValues result = classifier.classify(text);

        assertThat(result.invoices()).isEqualTo("F123-12345678");
        assertThat(result.receipts()).isEmpty();
    }

    @Test
    void shouldClassifyReceiptWhenTextContainsReceiptPattern() {
        String text = "Boleta B456-87654321 registrada correctamente.";

        ClassifiedTicketValues result = classifier.classify(text);

        assertThat(result.receipts()).isEqualTo("B456-87654321");
        assertThat(result.invoices()).isEmpty();
    }

    @Test
    void shouldClassifyInvoicesAndReceiptsWhenBothExist() {
        String text = """
                Factura F111-11111111
                Boleta B222-22222222
                Factura F333-33333333
                """;

        ClassifiedTicketValues result = classifier.classify(text);

        assertThat(result.invoices())
                .contains("F111-11111111", "F333-33333333");

        assertThat(result.receipts())
                .contains("B222-22222222");
    }

    @Test
    void shouldNotDuplicateMatches() {
        String text = """
                Factura F999-99999999
                Factura F999-99999999
                """;

        ClassifiedTicketValues result = classifier.classify(text);

        assertThat(result.invoices()).isEqualTo("F999-99999999");
    }

    @Test
    void shouldReturnEmptyValuesWhenTextIsNull() {
        ClassifiedTicketValues result = classifier.classify(null);

        assertThat(result).isEqualTo(ClassifiedTicketValues.empty());
    }

    @Test
    void shouldReturnEmptyValuesWhenTextIsBlank() {
        ClassifiedTicketValues result = classifier.classify("   ");

        assertThat(result).isEqualTo(ClassifiedTicketValues.empty());
    }

}
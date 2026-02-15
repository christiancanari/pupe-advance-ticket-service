package com.christiancanari.pupe.advance.ticket.service.infrastructure.extractor;

import com.christiancanari.pupe.advance.ticket.service.domain.classifier.ClassifiedTicketValues;
import com.christiancanari.pupe.advance.ticket.service.domain.classifier.TicketInvoiceClassifier;
import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketInvoice;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfTicketInvoiceExtractorTest {

    private TicketInvoiceClassifier classifier;
    private PdfTicketInvoiceExtractor extractor;

    @BeforeEach
    void setUp() {
        classifier = mock(TicketInvoiceClassifier.class);
        extractor = new PdfTicketInvoiceExtractor(classifier);
    }

    @Test
    @DisplayName("Debe extraer texto del PDF y construir el TicketInvoice")
    void shouldExtractPdfAndReturnTicketInvoice() throws Exception {

        // ---------- PDF en memoria ----------
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, 700);
                content.showText("Factura F112-12345678");
                content.endText();
            }

            document.save(output);
        }

        InputStream pdfStream =
                new ByteArrayInputStream(output.toByteArray());

        // ---------- Mock classifier ----------
        when(classifier.classify(anyString()))
                .thenReturn(new ClassifiedTicketValues(
                        "F112-12345678",
                        ""
                ));

        // ---------- Ejecutar ----------
        TicketInvoice result = extractor.extract(
                "FACTURAS",
                "ticket.pdf",
                pdfStream
        );

        // ---------- Asserts ----------
        assertNotNull(result);
        assertEquals("FACTURAS", result.sourceFileName());
        assertEquals("ticket.pdf", result.processedFileName());
        assertEquals("F112-12345678", result.facturaContent());
        assertEquals("", result.comprobanteContent());

        verify(classifier).classify(contains("Factura"));
    }

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException cuando el PDF es inválido")
    void shouldThrowExceptionWhenPdfIsInvalid() {

        InputStream invalidPdf =
                new ByteArrayInputStream("not-a-pdf".getBytes());

        assertThrows(CoreTechnicalException.class, () ->
                extractor.extract(
                        "FACTURAS",
                        "corrupt.pdf",
                        invalidPdf
                )
        );
    }

}

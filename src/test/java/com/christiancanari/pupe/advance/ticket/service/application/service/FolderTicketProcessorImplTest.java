package com.christiancanari.pupe.advance.ticket.service.application.service;

import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketFile;
import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketInvoice;
import com.christiancanari.pupe.advance.ticket.service.domain.port.out.TicketGoogleDrivePort;
import com.christiancanari.pupe.advance.ticket.service.domain.service.TicketInvoiceExtractor;
import com.christiancanari.pupe.advance.ticket.service.domain.service.TicketInvoicePolicy;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreBusinessException;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FolderTicketProcessorImplTest {

    private TicketGoogleDrivePort drivePort;
    private TicketInvoiceExtractor extractor;
    private TicketInvoicePolicy policy;

    private FolderTicketProcessorImpl processor;

    @BeforeEach
    void setUp() {
        drivePort = mock(TicketGoogleDrivePort.class);
        extractor = mock(TicketInvoiceExtractor.class);
        policy = mock(TicketInvoicePolicy.class);

        processor = new FolderTicketProcessorImpl(
                drivePort,
                extractor,
                policy
        );
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando la carpeta no existe")
    void shouldReturnEmptyWhenFolderNotFound() {

        when(drivePort.findFolderIdByName("FACTURAS"))
                .thenReturn(Optional.empty());

        List<TicketInvoice> result = processor.process("FACTURAS");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no existe subcarpeta Tickets")
    void shouldReturnEmptyWhenTicketsFolderNotFound() {

        when(drivePort.findFolderIdByName("FACTURAS"))
                .thenReturn(Optional.of("folder-id"));
        when(drivePort.findTicketFolderId("folder-id"))
                .thenReturn(Optional.empty());

        List<TicketInvoice> result = processor.process("FACTURAS");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay PDFs")
    void shouldReturnEmptyWhenNoPdfFiles() {

        when(drivePort.findFolderIdByName("FACTURAS"))
                .thenReturn(Optional.of("folder-id"));
        when(drivePort.findTicketFolderId("folder-id"))
                .thenReturn(Optional.of("tickets-id"));
        when(drivePort.listPdfFiles("tickets-id"))
                .thenReturn(List.of());

        List<TicketInvoice> result = processor.process("FACTURAS");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe procesar solo PDFs válidos según la policy")
    void shouldProcessOnlyValidPdfFiles() {

        TicketFile validPdf = new TicketFile("1", "ticket-pr.pdf");
        TicketFile invalidPdf = new TicketFile("2", "otro.pdf");

        when(drivePort.findFolderIdByName("FACTURAS"))
                .thenReturn(Optional.of("folder-id"));
        when(drivePort.findTicketFolderId("folder-id"))
                .thenReturn(Optional.of("tickets-id"));
        when(drivePort.listPdfFiles("tickets-id"))
                .thenReturn(List.of(validPdf, invalidPdf));

        when(policy.isValid("ticket-pr.pdf")).thenReturn(true);
        when(policy.isValid("otro.pdf")).thenReturn(false);

        when(drivePort.downloadFile("1"))
                .thenReturn(new ByteArrayInputStream("pdf".getBytes()));

        TicketInvoice invoice = mock(TicketInvoice.class);
        when(extractor.extract(eq("FACTURAS"), eq("ticket-pr.pdf"), any()))
                .thenReturn(invoice);

        List<TicketInvoice> result = processor.process("FACTURAS");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException cuando falla el extractor")
    void shouldThrowExceptionWhenExtractorFails() {

        TicketFile pdf = new TicketFile("1", "ticket.pdf");

        when(drivePort.findFolderIdByName("FACTURAS"))
                .thenReturn(Optional.of("folder-id"));
        when(drivePort.findTicketFolderId("folder-id"))
                .thenReturn(Optional.of("tickets-id"));
        when(drivePort.listPdfFiles("tickets-id"))
                .thenReturn(List.of(pdf));
        when(policy.isValid("ticket.pdf")).thenReturn(true);

        when(drivePort.downloadFile("1"))
                .thenReturn(new ByteArrayInputStream("pdf".getBytes()));

        when(extractor.extract(any(), any(), any()))
                .thenThrow(new RuntimeException("Extractor error"));

        CoreTechnicalException ex = assertThrows(
                CoreTechnicalException.class,
                () -> processor.process("FACTURAS")
        );

        assertTrue(ex.getMessage().contains("archivo PDF"));
    }

    @Test
    @DisplayName("Debe re-lanzar CoreBusinessException sin envolverla")
    void shouldRethrowCoreBusinessException() {

        CoreBusinessException businessException =
                new CoreBusinessException("Error negocio", null);

        when(drivePort.findFolderIdByName("FACTURAS"))
                .thenThrow(businessException);

        CoreBusinessException ex = assertThrows(
                CoreBusinessException.class,
                () -> processor.process("FACTURAS")
        );

        assertSame(businessException, ex);
    }

    @Test
    @DisplayName("Debe envolver en CoreTechnicalException cuando ocurre un error inesperado en process")
    void shouldWrapUnexpectedExceptionInProcess() {

        when(drivePort.findFolderIdByName("FACTURAS"))
                .thenReturn(Optional.of("folder-id"));
        when(drivePort.findTicketFolderId("folder-id"))
                .thenReturn(Optional.of("tickets-id"));

        // 🔥 Error inesperado ANTES del stream
        when(drivePort.listPdfFiles("tickets-id"))
                .thenThrow(new RuntimeException("Boom inesperado"));

        CoreTechnicalException ex = assertThrows(
                CoreTechnicalException.class,
                () -> processor.process("FACTURAS")
        );

        assertTrue(ex.getMessage().contains("Error procesando la carpeta"));
        assertTrue(ex.getMessage().contains("FACTURAS"));
    }

    @Test
    @DisplayName("Debe cubrir el constructor CoreBusinessException con causa")
    void shouldCreateCoreBusinessExceptionWithCause() {

        RuntimeException rootCause = new RuntimeException("Causa raíz");

        CoreBusinessException exception =
                new CoreBusinessException(
                        "Error de negocio",
                        ErrorType.NO_FOLDERS_FOUND,
                        rootCause
                );

        assertEquals("Error de negocio", exception.getMessage());
        assertEquals(ErrorType.NO_FOLDERS_FOUND, exception.getType());
        assertEquals(rootCause, exception.getCause());
    }


}
